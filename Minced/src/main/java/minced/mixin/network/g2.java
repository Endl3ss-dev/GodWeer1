package minced.mixin.network;
import minced.core.RecordClass229;
import sg.ec.tzh;

/** Restores a temporarily swapped Minecraft client context. */
final class g2 {
    static final class ch implements AutoCloseable {
        private final RecordClass229 snapshot;
        private final tzh.ch scope;

        ch(RecordClass229 snapshot, tzh.ch scope) {
            this.snapshot = snapshot;
            this.scope = scope;
        }

        RecordClass229 snapshot() {
            return snapshot;
        }

        @Override
        public void close() {
            if (scope != null) scope.close();
        }
    }
}
