import unittest
from analyze_telly_episodes import analyze


def sample(tick, grounded, segment=0):
    return {'type': 'sample', 'data': {'state': {
        'clientTick': tick, 'nanos': tick * 50000000, 'grounded': grounded,
        'segment': segment, 'clientSpeed': 1.0}}}


def use(tick, x, y=63, z=0, consumed=True):
    return {'type': 'use', 'data': {'observation': {
        'clientTick': tick, 'x': x, 'y': y, 'z': z, 'consumed': consumed,
        'predictedChange': True, 'bridgeCandidate': True, 'grounded': False}}}


class EpisodesTest(unittest.TestCase):
    def episode(self, uses):
        return [sample(0, True), sample(1, False), *uses, sample(12, True)]

    def test_flat_contiguous(self):
        result = analyze(self.episode([use(4, 1), use(7, 2), use(10, 3)]))[0]
        self.assertTrue(result['flatStraightContiguous3Plus'])
        self.assertEqual(result['placementTickOffsets'], [4, 7, 10])

    def test_duplicate_does_not_make_third_block(self):
        result = analyze(self.episode([use(4, 1), use(7, 2), use(10, 2)]))[0]
        self.assertEqual(result['uniqueBlocks'], 2)
        self.assertFalse(result['flatStraightContiguous3Plus'])

    def test_gap(self):
        self.assertFalse(analyze(self.episode([use(4, 1), use(7, 2), use(10, 4)]))[0]['flatStraightContiguous3Plus'])

    def test_different_height(self):
        self.assertFalse(analyze(self.episode([use(4, 1), use(7, 2), use(10, 3, y=64)]))[0]['flatStraightContiguous3Plus'])

    def test_segment_break(self):
        self.assertEqual(analyze([sample(0, True), sample(1, False), sample(12, True, 1)]), [])

    def test_incomplete_and_initial_air(self):
        self.assertEqual(analyze([sample(0, True), sample(1, False)]), [])
        self.assertEqual(analyze([sample(1, False), sample(12, True)]), [])

    def test_rejected(self):
        result = analyze(self.episode([use(4, 1, consumed=False)]))[0]
        self.assertEqual(result['uniqueBlocks'], 0)


if __name__ == '__main__':
    unittest.main()
