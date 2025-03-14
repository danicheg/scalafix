// Sources copy-pasted from Ammonite:
// https://github.com/lihaoyi/Ammonite/blob/73a874173cd337f953a3edc9fb8cb96556638fdd/amm/util/src/main/scala/ammonite/util/Model.scala#L71-L121
// Original licence: MIT
// Original author: Li Haoyi
package scala.meta.internal.pc

import scala.tools.nsc.Global

object Identifier {
  def apply(name: Global#Name): String = backtickWrap(name)
  def apply(name: String): String = backtickWrap(name)
  private val alphaKeywords = Set(
    "abstract",
    "case",
    "catch",
    "class",
    "def",
    "do",
    "else",
    "extends",
    "false",
    "finally",
    "final",
    "finally",
    "forSome",
    "for",
    "if",
    "implicit",
    "import",
    "lazy",
    "match",
    "new",
    "null",
    "object",
    "override",
    "package",
    "private",
    "protected",
    "return",
    "sealed",
    "super",
    "this",
    "throw",
    "trait",
    "try",
    "true",
    "type",
    "val",
    "var",
    "while",
    "with",
    "yield",
    "_",
    "macro"
  )
  private val symbolKeywords = Set(
    ":",
    ";",
    "=>",
    "=",
    "<-",
    "<:",
    "<%",
    ">:",
    "#",
    "@",
    "\u21d2",
    "\u2190"
  )
  private val blockCommentStart = "/*"
  private val lineCommentStart = "//"

  def needsBacktick(s: String): Boolean = {
    val chunks = s.split("_", -1)
    def validOperator(c: Char) = {
      c.getType == Character.MATH_SYMBOL ||
      c.getType == Character.OTHER_SYMBOL ||
      "!#%&*+-/:<=>?@\\^|~".contains(c)
    }
    val validChunks = chunks.zipWithIndex.forall { case (chunk, index) =>
      chunk.forall(c => c.isLetter || c.isDigit || c == '$') ||
      (chunk.forall(validOperator) &&
        // operators can only come last
        index == chunks.length - 1 &&
        // but cannot be preceded by only a _
        !(chunks.lift(index - 1).contains("") && index - 1 == 0))
    }

    val firstLetterValid =
      s(0).isLetter ||
        s(0) == '_' ||
        s(0) == '$' ||
        validOperator(s(0))

    val valid =
      validChunks &&
        firstLetterValid &&
        !alphaKeywords.contains(s) &&
        !symbolKeywords.contains(s) &&
        !s.contains(blockCommentStart) &&
        !s.contains(lineCommentStart)

    !valid
  }

  def backtickWrap(name: Global#Name): String = {
    backtickWrap(name.decoded.trim)
  }
  def backtickWrap(s: String): String = {
    if (s.isEmpty) "``"
    else if (s(0) == '`' && s.last == '`') s
    else if (needsBacktick(s)) '`' + s + '`'
    else s
  }

}
