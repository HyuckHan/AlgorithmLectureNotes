#include <assert.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>
#include <stdlib.h>

typedef enum { EMPTY, OCCUPIED, DELETED } SlotState;
typedef struct { int key, value; SlotState state; } Slot;
typedef struct {
    Slot *slots;
    size_t capacity, size, tombstones;
} OpenTable;

static size_t index_of(int key, size_t capacity) {
    int64_t r = (int64_t)key % (int64_t)capacity;
    return (size_t)(r < 0 ? r + (int64_t)capacity : r);
}

static bool open_init(OpenTable *t, size_t capacity) {
    if (!t || capacity < 2) return false;
    t->slots = calloc(capacity, sizeof *t->slots);
    if (!t->slots) return false;
    t->capacity = capacity; t->size = t->tombstones = 0;
    return true;
}

static void open_destroy(OpenTable *t) {
    if (!t) return;
    free(t->slots);
    *t = (OpenTable){0};
}

static ptrdiff_t open_find(const OpenTable *t, int key) {
    size_t home = index_of(key, t->capacity);
    for (size_t i = 0; i < t->capacity; i++) {
        size_t j = (home + i) % t->capacity;
        if (t->slots[j].state == EMPTY) return -1;
        if (t->slots[j].state == OCCUPIED && t->slots[j].key == key) return (ptrdiff_t)j;
    }
    return -1;
}

static bool open_get(const OpenTable *t, int key, int *out) {
    if (!t || !t->slots) return false;
    ptrdiff_t j = open_find(t, key);
    if (j < 0) return false;
    if (out) *out = t->slots[j].value;
    return true;
}

static bool open_reinsert(OpenTable *t, int key, int value) {
    size_t home = index_of(key, t->capacity);
    for (size_t i = 0; i < t->capacity; i++) {
        size_t j = (home + i) % t->capacity;
        if (t->slots[j].state == EMPTY) {
            t->slots[j] = (Slot){key, value, OCCUPIED};
            t->size++;
            return true;
        }
    }
    return false;
}

static bool open_resize(OpenTable *t, size_t new_capacity) {
    Slot *old = t->slots;
    size_t old_capacity = t->capacity, old_size = t->size;
    Slot *fresh = calloc(new_capacity, sizeof *fresh);
    if (!fresh) return false;
    t->slots = fresh; t->capacity = new_capacity; t->size = t->tombstones = 0;
    for (size_t i = 0; i < old_capacity; i++)
        if (old[i].state == OCCUPIED && !open_reinsert(t, old[i].key, old[i].value)) abort();
    free(old);
    assert(t->size == old_size);
    return true;
}

static bool open_put(OpenTable *t, int key, int value) {
    if (!t || !t->slots) return false;
    if ((t->size + t->tombstones + 1) * 10 > t->capacity * 7)
        if (!open_resize(t, t->capacity * 2 + 1)) return false;
    size_t home = index_of(key, t->capacity);
    ptrdiff_t first_deleted = -1;
    for (size_t i = 0; i < t->capacity; i++) {
        size_t j = (home + i) % t->capacity;
        if (t->slots[j].state == OCCUPIED && t->slots[j].key == key) {
            t->slots[j].value = value;
            return true;
        }
        if (t->slots[j].state == DELETED && first_deleted < 0) first_deleted = (ptrdiff_t)j;
        if (t->slots[j].state == EMPTY) {
            size_t target = first_deleted >= 0 ? (size_t)first_deleted : j;
            if (t->slots[target].state == DELETED) t->tombstones--;
            t->slots[target] = (Slot){key, value, OCCUPIED};
            t->size++;
            return true;
        }
    }
    if (first_deleted >= 0) {
        size_t j = (size_t)first_deleted;
        t->slots[j] = (Slot){key, value, OCCUPIED};
        t->size++; t->tombstones--;
        return true;
    }
    return false;
}

static bool open_remove(OpenTable *t, int key, int *old_value) {
    if (!t || !t->slots) return false;
    ptrdiff_t found = open_find(t, key);
    if (found < 0) return false;
    size_t j = (size_t)found;
    if (old_value) *old_value = t->slots[j].value;
    t->slots[j] = (Slot){0, 0, DELETED};
    t->size--; t->tombstones++;
    return true;
}

static bool open_validate(const OpenTable *t) {
    if (!t || !t->slots || t->capacity == 0) return false;
    size_t active = 0, deleted = 0;
    for (size_t j = 0; j < t->capacity; j++) {
        if (t->slots[j].state == OCCUPIED) {
            active++;
            if (open_find(t, t->slots[j].key) != (ptrdiff_t)j) return false;
            for (size_t k = j + 1; k < t->capacity; k++)
                if (t->slots[k].state == OCCUPIED && t->slots[k].key == t->slots[j].key) return false;
        } else if (t->slots[j].state == DELETED) deleted++;
        else if (t->slots[j].state != EMPTY) return false;
    }
    return active == t->size && deleted == t->tombstones &&
           t->size + t->tombstones <= t->capacity;
}

int main(void) {
    OpenTable t;
    assert(open_init(&t, 5) && open_validate(&t));
    int keys[] = {25,13,16,15,7,28,31,20,1,38,-1,-14};
    for (size_t i = 0; i < sizeof keys / sizeof *keys; i++) assert(open_put(&t, keys[i], (int)i));
    assert(t.capacity > 5 && open_validate(&t));
    for (size_t i = 0; i < sizeof keys / sizeof *keys; i++) {
        int value;
        assert(open_get(&t, keys[i], &value) && value == (int)i);
    }
    size_t before = t.tombstones;
    assert(open_remove(&t, 1, NULL) && t.tombstones == before + 1);
    assert(open_get(&t, 38, NULL));
    assert(open_put(&t, 99, 99) && open_put(&t, 38, 3800));
    int value;
    assert(open_get(&t, 38, &value) && value == 3800 && open_validate(&t));
    for (int i = 0; i < 1000; i++) assert(open_put(&t, i * 17, i));
    for (int i = 0; i < 1000; i++) assert(open_get(&t, i * 17, &value) && value == i);
    assert(open_validate(&t));
    open_destroy(&t);

    assert(open_init(&t, 5));
    bool present[401] = {false};
    int reference[401] = {0};
    srand(17);
    for (int step = 0; step < 12000; step++) {
        int key = rand() % 401 - 200, slot = key + 200, op = rand() % 3;
        int candidate = rand(), observed = 0;
        if (op == 0) {
            assert(open_put(&t, key, candidate));
            present[slot] = true; reference[slot] = candidate;
        } else if (op == 1) {
            bool removed = open_remove(&t, key, &observed);
            assert(removed == present[slot]);
            if (removed) { assert(observed == reference[slot]); present[slot] = false; }
        } else {
            bool found = open_get(&t, key, &observed);
            assert(found == present[slot]);
            if (found) assert(observed == reference[slot]);
        }
        assert(open_validate(&t));
    }
    for (int key = -200; key <= 200; key++) {
        int observed = 0, slot = key + 200;
        bool found = open_get(&t, key, &observed);
        assert(found == present[slot]);
        if (found) assert(observed == reference[slot]);
    }
    open_destroy(&t);
    return 0;
}
