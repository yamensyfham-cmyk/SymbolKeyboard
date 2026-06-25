#!/bin/bash
#
# Downloads required Noto fonts for SymbolKeyboard ancient scripts.
# These fonts are embedded in the APK for proper rendering on the keyboard.
#
# Usage: ./scripts/download-fonts.sh
#

set -e

FONTS_DIR="app/src/main/assets/fonts"
mkdir -p "$FONTS_DIR"

FONTS=(
    "NotoSansEgyptianHieroglyphs-Regular"
    "NotoSansCuneiform-Regular"
    "NotoSansPhoenician-Regular"
    "NotoSansRunic-Regular"
    "NotoSansGothic-Regular"
    "NotoSansOgham-Regular"
    "NotoSansLinearA-Regular"
    "NotoSansLinearB-Regular"
    "NotoSansAnatolianHieroglyphs-Regular"
)

BASE_URL="https://github.com/googlefonts/noto-fonts/raw/main/hinted/ttf"

echo "=== Downloading Noto Fonts ==="

for font in "${FONTS[@]}"; do
    TARGET="${FONTS_DIR}/${font}.ttf"
    if [ -f "$TARGET" ]; then
        echo "  [SKIP] ${font}.ttf already exists"
        continue
    fi

    URL="${BASE_URL}/${font}/${font}.ttf"
    echo "  [DOWNLOAD] ${font}.ttf..."

    HTTP_CODE=$(curl -sL -o "$TARGET" -w "%{http_code}" "$URL" 2>/dev/null || echo "000")

    if [ "$HTTP_CODE" != "200" ] || [ ! -s "$TARGET" ]; then
        echo "  [FAIL] HTTP $HTTP_CODE for ${font}.ttf"
        # Try alternative URL from Google Fonts
        ALT_URL="https://github.com/notofonts/noto-fonts/raw/main/hinted/ttf/${font}/${font}.ttf"
        echo "  [RETRY] Trying alternative URL..."
        curl -sL -o "$TARGET" "$ALT_URL" 2>/dev/null || true
        if [ ! -s "$TARGET" ]; then
            echo "  [FAIL] Could not download ${font}.ttf"
            rm -f "$TARGET"
        else
            echo "  [OK] ${font}.ttf downloaded (alternative source)"
        fi
    else
        echo "  [OK] ${font}.ttf downloaded"
    fi
done

echo ""
echo "Font download complete."
echo ""
echo "Checking font files in ${FONTS_DIR}:"
ls -lh "${FONTS_DIR}"/*.ttf 2>/dev/null || echo "  No font files found."

echo ""
echo "If fonts fail to download, you can manually download them from:"
echo "  https://github.com/googlefonts/noto-fonts/tree/main/hinted/ttf"
echo ""
echo "Place the .ttf files in: ${FONTS_DIR}"
