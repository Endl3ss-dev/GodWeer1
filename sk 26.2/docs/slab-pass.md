# Slab Pass

`SlabPassModule` crosses "slab - empty gap - slab" barriers (total height
two blocks) and plain two-block walls where placing blocks is not allowed.

| Setting | Default | Description |
|---|---|---|
| `Mode` | `Auto` | `Auto`: crawl through when the slab-gap-slab pattern is ahead, otherwise jump over. `Over`: only the jump. `Through`: only the crawl. |
| `Boost` | `1.0` | Multiplier (0.8–1.3) for the jump impulse in Over/Auto; hidden in Through mode. |

## Over

Walking into a wall whose top is higher than a normal jump (1.25 blocks)
but at most about 2.7 blocks above the feet applies a single vertical
impulse sized to clear that top. If the vanilla jump overwrote the impulse
in the same tick, it is re-applied once right after the tick.

## Through

A one-block-high gap between two slabs is crossed crawling: the swimming
pose (0.6 blocks high) is forced while the pattern is ahead or the player
still cannot stand up. Once inside, vanilla keeps the crawl pose by
itself, because standing does not fit under the upper slab.

This needs a modern (1.14+) server core. On 1.8 backends there is no
crawling pose, so Through cannot work there — use Over. Strict anticheats
may also push back while entering the gap; the module does not try to
hide the pose.
