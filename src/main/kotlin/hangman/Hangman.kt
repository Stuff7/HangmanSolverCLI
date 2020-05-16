package hangman

import java.io.File

import kotlin.system.measureTimeMillis

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import com.xenomachina.argparser.default

class MyArgs(parser: ArgParser) {
  val board by parser.positional(
    "BOARD",
    help = "Hangman board in this form: h?n?m?n")

  val wrong by parser.positional(
    "WRONG",
    help = "Wrong letters in this form: abcde"
  ).default("")
}

fun getResource(path: String): String {
  val resource = {}.javaClass.getResource("/$path")
  return if(resource == null) "" else resource.readText()
}

/* Writes hangman guesses to output.txt. */
fun solveHangman(board: String, wrong: String) {
  val guessedLetters = board.replace(Regex("[^a-zA-Z]"), "")
  val words = getResource("words.txt").lines().groupBy { it.length }
  val output = File("output.txt")
  val pattern = Regex(board.replace("?", "[^$guessedLetters]"))
  
  val guesses =
    words
      .get(board.length)
      ?.filterNot { wrong.any { char -> char in it } }
      ?.filter { pattern.matches(it) }

  if(guesses == null)
    return output.writeText("Could not find any words.")

  val wordCount = "%,d".format(guesses.size)

  output.writeText("""
    |Found $wordCount possible words:
    |
    |${guesses.joinToString("\n")}
    |
  """.trimMargin())
}

fun main(args: Array<String>) = mainBody {
  val elapsed = measureTimeMillis {
    ArgParser(args).parseInto(::MyArgs).run {
      solveHangman(board, wrong)
    }
  }
  println("Finished in ${elapsed}ms")
}
