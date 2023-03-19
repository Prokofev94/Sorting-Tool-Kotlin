package sorting

import java.io.File
import java.io.FileWriter
import java.lang.NumberFormatException
import java.util.Comparator
import java.util.Scanner

val scanner = Scanner(System.`in`)
var dataType = "word"
var sortingType = "natural"
var inputFile: File? = null
var outputFile: File? = null
var input = false
var output = false
val content = mutableListOf<String>()

fun main(args: Array<String>) {
    readArgs(args)
    sortInput()
}

fun readArgs(args: Array<String>) {
    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "-sortingType" -> {
                if (i + 1 < args.size && (args[i + 1] == "natural" || args[i + 1] == "byCount")) {
                    sortingType = args[i + 1]
                    i++
                } else {
                    println("No sorting type defined!")
                    sortingType = ""
                    return
                }
            }
            "-dataType" -> {
                if (i + 1 < args.size && (args[i + 1] == "long" || args[i + 1] == "word" || args[i + 1] == "line")) {
                    dataType = args[i + 1]
                    i++
                } else {
                    println("No data type defined!")
                    dataType = ""
                    return
                }
            }
            "-inputFile" -> {
                inputFile = File(args[i + 1])
                input = true
                i++
            }
            "-outputFile" -> {
                outputFile = File(args[i + 1])
                output = true
                i++
            }
            else -> println("\"${args[i]}\" is not a valid parameter. It will be skipped.")
        }
        i++
    }
}

fun sortInput() {
    if (dataType.isEmpty() || sortingType.isEmpty()) return
    readContent()
    when (dataType + sortingType) {
        "longnatural" -> sortLongsNatural()
        "longbyCount" -> sortLongsByCount()
        "wordnatural" -> sortWordsNatural()
        "wordbyCount" -> sortWordsByCount()
        "linenatural" -> sortLinesNatural()
        "linebyCount" -> sortLinesByCount()
    }
}

fun sortLongsNatural() {
    val list = mutableListOf<Int>()
    content.forEach {
        for (n in it.split(Regex("\\s+"))) {
            try {
                list += n.toInt()
            } catch (e: NumberFormatException) {
                println("\"$n\" is not a long. It will be skipped.")
            }
        }
    }
    list.sort()
    output("Total numbers: ${list.size}.\n")
    output("Sorted data:")
    list.forEach { output(" $it") }
}

fun sortLongsByCount() {
    val map = mutableMapOf<Int, Int>()
    var len = 0
    content.forEach {
        for (n in it.split(Regex("\\s+"))) {
            try {
                map[n.toInt()] = map.getOrDefault(n.toInt(), 0) + 1
                len++
            } catch (e: NumberFormatException) {
                println("\"$n\" is not a long. It will be skipped.")
            }
        }
    }
    val mapList = map.toList().sortedWith(NumberCountComparator())
    output("Total numbers: $len.\n")
    mapList.forEach { output("${it.first}: ${it.second} time(s), ${100 / len * it.second}%\n") }
}

fun sortWordsNatural() {
    val list = mutableListOf<String>()
    content.forEach { list.addAll(it.split(Regex("\\s+"))) }
    list.sort()
    output("Total words: ${list.size}.\n")
    output("Sorted data:")
    list.forEach { output(" $it") }
}

fun sortWordsByCount() {
    val map = mutableMapOf<String, Int>()
    var len = 0
    content.forEach {
        for (word in it.split(Regex("\\s+"))) {
            map[word] = map.getOrDefault(word, 0) + 1
            len++
        }
    }
    val mapList = map.toList().sortedWith(StringCountComparator())
    output("Total words: $len.\n")
    mapList.forEach { output("${it.first}: ${it.second} time(s), ${100 / len * it.second}%\n") }
}

fun sortLinesNatural() {
    content.sort()
    output("Total lines: ${content.size}.\n")
    output("Sorted data:\n")
    content.forEach { output("$it\n") }
}

fun sortLinesByCount() {
    val map = mutableMapOf<String, Int>()
    val len = content.size
    content.forEach { map[it] = map.getOrDefault(it, 0) + 1 }
    val mapList = map.toList().sortedWith(StringCountComparator())
    output("Total lines: $len.\n")
    mapList.forEach { output("${it.first}: ${it.second} time(s), ${100 / len * it.second}%\n") }
}

fun readContent() {
    val sc = if (input) Scanner(inputFile) else scanner
    sc.use {
        while (sc.hasNext()) {
            content += sc.nextLine()
        }
    }
}

fun output(line: String) {
    if (output) {
        val writer = FileWriter(outputFile)
        writer.use { writer.append(line) }
    } else {
        print(line)
    }
}

class NumberCountComparator : Comparator<Pair<Int, Int>> {
    override fun compare(o1: Pair<Int, Int>, o2: Pair<Int, Int>) =
        if (o1.second == o2.second) o1.first - o2.first else o1.second - o2.second
}

class StringCountComparator : Comparator<Pair<String, Int>> {
    override fun compare(o1: Pair<String, Int>, o2: Pair<String, Int>) =
        if (o1.second == o2.second) o1.first.compareTo(o2.first) else o1.second - o2.second
}