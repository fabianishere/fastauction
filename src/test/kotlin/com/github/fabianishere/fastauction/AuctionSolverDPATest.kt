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
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.util.stream.Stream


/**
 * Test suite for the dynamic programming solver.
 */
class AuctionSolverDPATest {
    private val epsilon = 0.1

    /**
     * Run the test instances provided by the course and compare to the brute force solution.
     */
    @TestFactory
    fun testInstances(): Stream<DynamicTest> {
        val reader = AuctionInstanceReader()
        val exact = AuctionSolverExact()
        val dpa = AuctionSolverDPA(epsilon)

        return Files.walk(Paths.get("examples/"), 1)
            .filter { Files.isRegularFile(it) }
            .map { f: Path ->
                dynamicTest(f.toString(), f.toUri()) {
                    val instance = reader.read(f.toFile())
                    val exactSolution: Int = assertTimeoutPreemptively(Duration.ofSeconds(5), "Exact Solver timeout exceeded") {
                        exact.solve(instance)
                    }
                    val dpaSolution: Int = assertTimeoutPreemptively(Duration.ofSeconds(5), "DPA Solver timeout exceeded") {
                        dpa.solve(instance)
                    }

                    assertTrue(
                        dpaSolution >= (1 - epsilon) * exactSolution,
                        "%d < %f".format(dpaSolution, (1 - epsilon) * exactSolution)
                    )
                    assertFalse(dpaSolution > exactSolution, "%d > %d".format(dpaSolution, exactSolution))
                }
            }
    }
}