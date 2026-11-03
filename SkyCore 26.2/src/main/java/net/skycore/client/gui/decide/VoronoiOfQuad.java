package net.skycore.client.gui.decide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class VoronoiOfQuad {
    private static final int CANDIDATES_PER_POINT = 24;
    private final List<Polygon> polygons;

    public VoronoiOfQuad(float x, float y, float x2, float y2, List<Vec2f> points) {
        this.polygons = computeVoronoiPolygons(x, y, x2, y2, points);
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    public static VoronoiOfQuad spread(float x1, float y1, float x2, float y2,
                                       float bx1, float by1, float bx2, float by2,
                                       int count, float aspect, float relax,
                                       float jitter, float minDist, Random random) {
        float spacing = (float) Math.sqrt((bx2 - bx1) * aspect * (by2 - by1) / (float) Math.max(1, count));
        float minSpacing = spacing * minDist;
        List<Vec2f> points = genSpreadPointsInBounds(bx1, by1, bx2, by2, count, aspect, minSpacing, random);
        if (relax > 0.001f) {
            List<Polygon> relaxed = new VoronoiOfQuad(x1, y1, x2, y2, points).getPolygons();
            if (relaxed.size() == points.size()) {
                for (int i = 0; i < points.size(); i++) {
                    Vec2f gen = points.get(i);
                    Vec2f cen = relaxed.get(i).center;
                    gen.x = clamp(gen.x + (cen.x - gen.x) * relax, bx1, bx2);
                    gen.y = clamp(gen.y + (cen.y - gen.y) * relax, by1, by2);
                }
            }
        }
        if (jitter > 0.001f) {
            float jx = spacing * jitter / aspect;
            float jy = spacing * jitter;
            for (int i = 0; i < points.size(); i++) {
                Vec2f p = points.get(i);
                float nx = clamp(p.x + (random.nextFloat() * 2.0f - 1.0f) * jx, bx1, bx2);
                float ny = clamp(p.y + (random.nextFloat() * 2.0f - 1.0f) * jy, by1, by2);
                if (nearestDistance(points, i, nx, ny, aspect) >= minSpacing * minSpacing) {
                    p.x = nx;
                    p.y = ny;
                }
            }
        }
        return new VoronoiOfQuad(x1, y1, x2, y2, points);
    }

    private List<Polygon> computeVoronoiPolygons(float x1, float y1, float x2, float y2, List<Vec2f> points) {
        List<Polygon> result = new ArrayList<>();
        List<Vec2f> bounds = Arrays.asList(new Vec2f(x1, y1), new Vec2f(x2, y1), new Vec2f(x2, y2), new Vec2f(x1, y2));
        int n = points.size();
        for (int i = 0; i < n; i++) {
            List<Vec2f> poly = new ArrayList<>(bounds);
            Vec2f pi = points.get(i);
            for (int j = 0; j < n; j++) {
                if (j == i) {
                    continue;
                }
                Vec2f pj = points.get(j);
                float a = 2.0f * (pj.x - pi.x);
                float b = 2.0f * (pj.y - pi.y);
                float c = pi.x * pi.x + pi.y * pi.y - (pj.x * pj.x + pj.y * pj.y);
                poly = clipPolygon(poly, a, b, c);
                if (poly.isEmpty()) {
                    break;
                }
            }
            if (poly.size() >= 3) {
                result.add(new Polygon(new ArrayList<>(poly)));
            }
        }
        return result;
    }

    private static List<Vec2f> clipPolygon(List<Vec2f> poly, float a, float b, float c) {
        List<Vec2f> out = new ArrayList<>(poly.size() + 2);
        int n = poly.size();
        for (int i = 0; i < n; i++) {
            Vec2f cur = poly.get(i);
            Vec2f next = poly.get((i + 1) % n);
            boolean curIn = isInside(cur, a, b, c);
            boolean nextIn = isInside(next, a, b, c);
            if (curIn) {
                if (!nextIn) {
                    Vec2f ix = findIntersection(cur, next, a, b, c);
                    if (ix != null) {
                        out.add(ix);
                    }
                } else {
                    out.add(next);
                }
            } else if (nextIn) {
                Vec2f ix = findIntersection(cur, next, a, b, c);
                if (ix != null) {
                    out.add(ix);
                }
                out.add(next);
            }
        }
        return out;
    }

    private static boolean isInside(Vec2f v, float a, float b, float c) {
        return a * v.x + b * v.y + c <= 0.0f;
    }

    private static Vec2f findIntersection(Vec2f p1, Vec2f p2, float a, float b, float c) {
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;
        float denom = a * dx + b * dy;
        if (Math.abs(denom) < 1.0E-4f) {
            return null;
        }
        float t = -(a * p1.x + b * p1.y + c) / denom;
        return t >= 0.0f && t <= 1.0f ? new Vec2f(p1.x + t * dx, p1.y + t * dy) : null;
    }

    private static float nearestDistance(List<Vec2f> list, int skip, float x, float y, float aspect) {
        float best = Float.MAX_VALUE;
        for (int i = 0; i < list.size(); i++) {
            if (i == skip) {
                continue;
            }
            Vec2f p = list.get(i);
            float dx = (x - p.x) * aspect;
            float dy = y - p.y;
            best = Math.min(best, dx * dx + dy * dy);
        }
        return best;
    }

    private static List<Vec2f> genSpreadPointsInBounds(float x1, float y1, float x2, float y2,
                                                        int n, float aspect, float minSpacing, Random random) {
        List<Vec2f> list = new ArrayList<>(n);
        float minSq = minSpacing * minSpacing;
        for (int i = 0; i < n; i++) {
            Vec2f best = null;
            float bestDist = -1.0f;
            for (int j = 0; j < CANDIDATES_PER_POINT; j++) {
                Vec2f cand = new Vec2f(x1 + random.nextFloat() * (x2 - x1), y1 + random.nextFloat() * (y2 - y1));
                if (list.isEmpty()) {
                    best = cand;
                    bestDist = Float.MAX_VALUE;
                    break;
                }
                float d = nearestDistance(list, -1, cand.x, cand.y, aspect);
                if (d > bestDist) {
                    bestDist = d;
                    best = cand;
                }
                if (bestDist >= minSq) {
                    break;
                }
            }
            if (best != null && bestDist >= minSq) {
                list.add(best);
            }
        }
        return list;
    }

    public List<Polygon> getPolygons() {
        return this.polygons;
    }

    public static final class Vec2f {
        public float x;
        public float y;

        public Vec2f(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static final class Polygon {
        public final Vec2f center;
        public final ArrayList<Vec2f> list;

        public Polygon(ArrayList<Vec2f> list) {
            this.list = list;
            float cx = 0.0f;
            float cy = 0.0f;
            for (Vec2f v : list) {
                cx += v.x;
                cy += v.y;
            }
            int n = Math.max(1, list.size());
            this.center = new Vec2f(cx / n, cy / n);
        }
    }
}
