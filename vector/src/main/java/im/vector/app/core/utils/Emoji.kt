/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.core.utils

import com.vanniktech.emoji.EmojiUtils

/**
 * Replace custom emojis with some other arbitrary emoji, so we can maintain our logic
 * for when to display emoji-only messages larger than others.
 */
fun customToPseudoEmoji(str: String): String {
    return str.replace(Regex("""<img\s+([^>]*)data-mx-emoticon([^>]*)>"""), "\uD83D\uDC40")
}

/**
 * Test if a string contains emojis.
 * It seems that the regex [emoji_regex]+ does not work.
 * Some characters like ?, # or digit are accepted.
 *
 * @param str the body to test
 * @return true if the body contains only emojis
 */
fun containsOnlyEmojis(str: String?): Boolean {
    // Now rely on vanniktech library
    // Emojis sent from desktop such as thumbs-up or down are sent with a variant selection symbol "\ufe0f",
    // that the library identifies as non-emoji character, so remove that manually before.
    return EmojiUtils.isOnlyEmojis(str?.replace("\ufe0f", ""))
}

/**
 * Same as split, but considering emojis
 */
fun CharSequence.splitEmoji(): List<CharSequence> {
    val result = mutableListOf<CharSequence>()

    var index = 0

    while (index < length) {
        val firstChar = get(index)

        if (firstChar.code == 0x200e) {
            // Left to right mark. What should I do with it?
        } else if (firstChar.code in 0xD800..0xDBFF && index + 1 < length) {
            // We have the start of a surrogate pair
            val secondChar = get(index + 1)

            if (secondChar.code in 0xDC00..0xDFFF) {
                // We have an emoji
                result.add("$firstChar$secondChar")
                index++
            } else {
                // Not sure what we have here...
                result.add("$firstChar")
            }
        } else {
            // Regular char
            result.add("$firstChar")
        }

        index++
    }

    return result
}
