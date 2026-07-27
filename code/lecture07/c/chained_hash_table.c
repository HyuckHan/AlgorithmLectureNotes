#include <assert.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct Entry {
    int key, value;
    struct Entry *next;
} Entry;

typedef struct {
    Entry **buckets;
    size_t capacity, size;
} ChainTable;

static size_t index_of(int key, size_t capacity) {
    int64_t r = (int64_t)key % (int64_t)capacity;
    return (size_t)(r < 0 ? r + (int64_t)capacity : r);
}

static bool chain_init(ChainTable *t, size_t capacity) {
    if (!t || capacity == 0) return false;
    t->buckets = calloc(capacity, sizeof *t->buckets);
    if (!t->buckets) return false;
    t->capacity = capacity;
    t->size = 0;
    return true;
}

static void chain_destroy(ChainTable *t) {
    if (!t || !t->buckets) return;
    for (size_t i = 0; i < t->capacity; i++) {
        Entry *e = t->buckets[i];
        while (e) {
            Entry *next = e->next;
            free(e);
            e = next;
        }
    }
    free(t->buckets);
    *t = (ChainTable){0};
}

static bool chain_get(const ChainTable *t, int key, int *out) {
    if (!t || !t->buckets) return false;
    for (Entry *e = t->buckets[index_of(key, t->capacity)]; e; e = e->next)
        if (e->key == key) {
            if (out) *out = e->value;
            return true;
        }
    return false;
}

static bool chain_resize(ChainTable *t, size_t new_capacity) {
    Entry **new_buckets = calloc(new_capacity, sizeof *new_buckets);
    if (!new_buckets) return false;
    for (size_t i = 0; i < t->capacity; i++) {
        Entry *e = t->buckets[i];
        while (e) {
            Entry *next = e->next;
            size_t j = index_of(e->key, new_capacity);
            e->next = new_buckets[j];
            new_buckets[j] = e;
            e = next;
        }
    }
    free(t->buckets);
    t->buckets = new_buckets;
    t->capacity = new_capacity;
    return true;
}

static bool chain_put(ChainTable *t, int key, int value) {
    if (!t || !t->buckets) return false;
    size_t j = index_of(key, t->capacity);
    for (Entry *e = t->buckets[j]; e; e = e->next)
        if (e->key == key) { e->value = value; return true; }
    if (t->size + 1 > t->capacity) {
        if (!chain_resize(t, t->capacity * 2)) return false;
        j = index_of(key, t->capacity);
    }
    Entry *e = malloc(sizeof *e);
    if (!e) return false;
    *e = (Entry){key, value, t->buckets[j]};
    t->buckets[j] = e;
    t->size++;
    return true;
}

static bool chain_remove(ChainTable *t, int key, int *old_value) {
    if (!t || !t->buckets) return false;
    size_t j = index_of(key, t->capacity);
    Entry **link = &t->buckets[j];
    while (*link) {
        Entry *e = *link;
        if (e->key == key) {
            *link = e->next;
            if (old_value) *old_value = e->value;
            free(e);
            t->size--;
            return true;
        }
        link = &e->next;
    }
    return false;
}

static bool chain_validate(const ChainTable *t) {
    if (!t || !t->buckets || t->capacity == 0) return false;
    size_t count = 0;
    for (size_t j = 0; j < t->capacity; j++) {
        Entry *slow = t->buckets[j], *fast = t->buckets[j];
        while (fast && fast->next) {
            slow = slow->next; fast = fast->next->next;
            if (slow == fast) return false;
        }
        for (Entry *a = t->buckets[j]; a; a = a->next) {
            if (index_of(a->key, t->capacity) != j) return false;
            for (Entry *b = a->next; b; b = b->next)
                if (a->key == b->key) return false;
            count++;
        }
    }
    return count == t->size;
}

int main(void) {
    ChainTable t;
    assert(chain_init(&t, 4) && chain_validate(&t));
    for (int i = -100; i <= 100; i++) assert(chain_put(&t, i * 7, i));
    assert(t.capacity > 4 && chain_validate(&t));
    for (int i = -100; i <= 100; i++) {
        int value;
        assert(chain_get(&t, i * 7, &value) && value == i);
    }
    assert(chain_put(&t, 14, 999));
    int value;
    assert(chain_get(&t, 14, &value) && value == 999);
    for (int i = -50; i <= 50; i++) assert(chain_remove(&t, i * 7, NULL));
    assert(!chain_get(&t, 0, NULL) && chain_validate(&t));
    chain_destroy(&t);
    assert(t.buckets == NULL && t.size == 0);

    assert(chain_init(&t, 4));
    bool present[401] = {false};
    int reference[401] = {0};
    srand(7);
    for (int step = 0; step < 10000; step++) {
        int key = rand() % 401 - 200, slot = key + 200, op = rand() % 3;
        int candidate = rand(), observed = 0;
        if (op == 0) {
            assert(chain_put(&t, key, candidate));
            present[slot] = true; reference[slot] = candidate;
        } else if (op == 1) {
            bool removed = chain_remove(&t, key, &observed);
            assert(removed == present[slot]);
            if (removed) { assert(observed == reference[slot]); present[slot] = false; }
        } else {
            bool found = chain_get(&t, key, &observed);
            assert(found == present[slot]);
            if (found) assert(observed == reference[slot]);
        }
        assert(chain_validate(&t));
    }
    for (int key = -200; key <= 200; key++) {
        int observed = 0, slot = key + 200;
        bool found = chain_get(&t, key, &observed);
        assert(found == present[slot]);
        if (found) assert(observed == reference[slot]);
    }
    chain_destroy(&t);
    return 0;
}
