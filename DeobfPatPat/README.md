# PatPat 1.3.1 — readable source snapshot

This directory contains the original readable PatPat source and resources for the Minecraft 1.21.4 Fabric release, copied from the upstream v1.3.1 source commit:

- Upstream: https://github.com/LopyMine/PatPat
- Commit: `4fea59411e3c4938229e733ed80dbd06d31ae8e1`
- Version: `1.3.1+1.21.4+fabric`
- License: LGPL-3.0-only (see `LICENSE_PatPat`)

The corresponding class files in the repository's `PatPat/` directory already retain descriptive package/class names; this is a source recovery from the matching upstream release, not an attempt to hide provenance or restrict reverse engineering. The upstream source is multi-version and uses its Stonecutter/Mossy preprocessing directives, so it is kept as source for inspection rather than presented as a standalone, preprocessed Gradle project.

Swerve integrates the unmodified PatPat Fabric JAR as a nested mod. The Swerve `PatPat` module controls PatPat's existing enable setting; all PatPat implementation logic remains in the upstream component. Do not also place a separate copy of PatPat in the `mods` folder when using the bundled Swerve build.
