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

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.util.stream.Stream
import kotlin.io.path.extension


/**
 * Test suite for the exact SIMD solver.
 */
class AuctionSolverExactSIMDTest {

    /**
     * Run the test instances provided by the course and compare to the brute force solution.
     */
    @TestFactory
    fun testInstances(): Stream<DynamicTest> {
        val reader = AuctionInstanceReader()
        val exact = AuctionSolverExact()
        val exactSIMD = AuctionSolverExactSIMD()

        return Files.walk(Paths.get("examples/"), 1)
            .filter { Files.isRegularFile(it) && it.extension == ".txt" }
            .map { f: Path ->
                dynamicTest(f.toString(), f.toUri()) {
                    val instance = reader.read(f.toFile())
                    val exactSolution = assertTimeoutPreemptively(Duration.ofSeconds(5), "Exact Solver timeout exceeded") {
                        exact.solve(instance)
                    }
                    val exactSIMDSolution = assertTimeoutPreemptively(Duration.ofSeconds(5), "DPA Solver timeout exceeded") {
                        exactSIMD.solve(instance)
                    }

                    assertEquals(exactSolution, exactSIMDSolution) { "Mismatch" }
                }
            }
    }


    @Test
    fun testExact() {
        val reader = AuctionInstanceReader()

        val dmax = 400
        val n = 5
        val k = 6
        val i = 0

        val solverA = AuctionSolverExact()
        val solverB = AuctionSolverExactSIMD()
        val instance = reader.read(File("examples/n_${n}_k_${k}_dmax_${dmax}_${i}.txt"))
        val a = solverA.solve(instance)
        val b = solverB.solve(instance)
        assertEquals(a, b) { "Mismatch" }
    }
}