/*
 * Copyright (c) 2022 Fabian Mastenbroek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.fabianishere.fastauction

import java.io.BufferedReader
import java.io.File
import java.nio.charset.Charset


/**
 * Helper class to parse a problem instance from text files.
 */
public class AuctionInstanceReader {
    /**
     * Read the problem instance from the specified [file].
     */
    public fun read(file: File): AuctionInstance {
        file.bufferedReader(Charset.defaultCharset()).use { reader ->
            val bidders = reader.readLine().toInt()
            val items = reader.readLine().toInt()
            return AuctionInstance(bidders, items, reader.parseBudgets(bidders), reader.parseBids(bidders, items))
        }
    }


    private fun BufferedReader.parseBudgets(bidders: Int): IntArray {
        val budgets = IntArray(bidders)
        var i = 0

        for (part in readLine().split(" ").toTypedArray()) {
            budgets[i++] = part.toInt()
            if (i >= bidders) {
                break
            }
        }
        return budgets
    }

    private fun BufferedReader.parseBids(bidders: Int, items: Int): Array<IntArray> {
        val bids = Array(bidders) { IntArray(items) }

        for (i in 0 until bidders) {
            var j = 0
            for (part in readLine().split(" ").toTypedArray()) {
                bids[i][j++] = part.toInt()
                if (j >= items) {
                    break
                }
            }
        }
        return bids
    }
}