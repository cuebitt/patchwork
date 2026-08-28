#!/usr/bin/env python3
"""Render every .ase/.aseprite file under assets-src into a PNG in build output.

assets-src mirrors src/client/resources/assets/patchwork: a file at
assets-src/<path>.ase[prite] is rendered to build/generated/resources/assets/patchwork/<path>.png.

No Aseprite binary required; uses the free aseprite-reader library
(pip install aseprite-reader / uv run --with aseprite-reader).
"""

import sys
from pathlib import Path

try:
    from aseprite_reader import AsepriteFile
except ImportError as exc:
    print(f"aseprite-reader not installed: {exc}", file=sys.stderr)
    sys.exit(1)

SRC = Path(__file__).resolve().parent
DEST_ROOT = SRC.parent / "build" / "generated" / "resources" / "assets" / "patchwork"

VALID_SUFFIXES = {".ase", ".aseprite"}


def collect_sources() -> list[Path]:
    sources: list[Path] = []
    for p in SRC.rglob("*"):
        if p.is_file() and p.suffix.lower() in VALID_SUFFIXES:
            sources.append(p)
    return sorted(sources)


def main() -> int:
    DEST_ROOT.mkdir(parents=True, exist_ok=True)

    sources = collect_sources()
    if not sources:
        print(f"no aseprite files found under {SRC}", file=sys.stderr)
        return 0

    failures = 0
    for src in sources:
        out = DEST_ROOT / src.relative_to(SRC).with_suffix(".png")
        out.parent.mkdir(parents=True, exist_ok=True)
        try:
            af = AsepriteFile(src)
            if not af.frames:
                raise ValueError("no frames in file")
            # frames is 0-indexed list; more robust than hard-coded frame(1)
            frame = af.frames[0]
            if out.exists():
                out.unlink()
            af.render(frame, out)
            print(
                f"exported {src.relative_to(SRC)} -> {out.relative_to(SRC.parent.parent)}"
            )
        except Exception as exc:  # noqa: BLE001 - per-file catch to continue exporting others
            failures += 1
            print(f"failed {src.relative_to(SRC)}: {exc}", file=sys.stderr)

    return 1 if failures else 0


if __name__ == "__main__":
    raise SystemExit(main())
