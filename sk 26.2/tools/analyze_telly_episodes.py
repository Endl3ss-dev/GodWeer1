"""Extract candidate flat Telly episodes; no dependencies or game execution.
Usage: python tools/analyze_telly_episodes.py recording.jsonl [...]
Output is evidence for a controller, NOT an executable/trained policy.
"""
import json
import sys


def analyze(rows):
    episodes = []
    previous = None
    active = None
    for row in rows:
        data = row['data']
        if row['type'] == 'sample':
            state = data['state']
            if previous is not None and state['segment'] != previous['segment']:
                active = None
                previous = None
            if previous is not None and previous['grounded'] and not state['grounded']:
                active = {'takeoff': previous, 'samples': [], 'uses': []}
            if active is not None:
                active['samples'].append(state)
                if state['grounded']:
                    uses = active['uses']
                    unique = {(u['x'], u['y'], u['z']) for u in uses}
                    flat = len({p[1] for p in unique}) == 1
                    straight = (len({p[0] for p in unique}) == 1 or
                                len({p[2] for p in unique}) == 1)
                    ordered = sorted(unique)
                    contiguous = all(sum(abs(a[i] - b[i]) for i in range(3)) == 1
                                     for a, b in zip(ordered, ordered[1:]))
                    start = active['takeoff']
                    episodes.append({
                        'takeoffNanos': start['nanos'],
                        'landingNanos': state['nanos'],
                        'clientTicks': state['clientTick'] - start['clientTick'],
                        'uniqueBlocks': len(unique),
                        'flatStraightContiguous3Plus': len(unique) >= 3 and flat and straight and contiguous,
                        'placementTickOffsets': [u['clientTick'] - start['clientTick'] for u in uses],
                        'clientSpeeds': sorted({s['clientSpeed'] for s in active['samples']}),
                        'segment': state['segment'],
                        'localPredictionOnly': True,
                    })
                    active = None
            previous = state
        elif row['type'] == 'use' and active is not None:
            use = data['observation']
            if (use['consumed'] and use['predictedChange'] and use['bridgeCandidate']
                    and not use['grounded']):
                # A repeated successful observation of the same cell is not another block.
                if not any((u['x'], u['y'], u['z']) == (use['x'], use['y'], use['z'])
                           for u in active['uses']):
                    active['uses'].append(use)
    return episodes


def main():
    result = {}
    for filename in sys.argv[1:]:
        with open(filename, encoding='utf-8-sig') as source:
            rows = [json.loads(line) for line in source if line.strip()]
        result[filename] = analyze(rows)
    print(json.dumps(result, indent=2))


if __name__ == '__main__':
    main()
