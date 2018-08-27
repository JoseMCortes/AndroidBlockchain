package com.josecortes.blockchainlib.model

import com.josecortes.blockchainlib.utils.sha512
import java.util.*

/**
 * Data Structure (based on Binary Search Tree BST) to contain the Transactions hashes. Each node contains
 * the calculate SHA-512 of the Left Subtree (if any) + Right Subtree (if any)
 * @See <a href="https://bitcoin.org/en/glossary/merkle-tree"</a>
 * @param data The data to create the MerkleTree as a root of the tree.
 */
class MerkleTree(var data: String?) {

    var leftTree: MerkleTree? = null
    var rightTree: MerkleTree? = null

    /**
     * Visits the Tree items in pre-order (first root, then traverse left subtree and then the right one).
     * @param visitor void callback to perform when visiting the node
     */
    fun prefix(visitor: (String?) -> Unit) {
        visitor(data)
        leftTree?.prefix(visitor)
        rightTree?.prefix(visitor)
    }

    /**
     * Visits the Tree items in pre-order (first left subtree, then the root, and  then the right one).
     * @param visitor void callback to perform when visiting the node
     */
    fun infix(visitor: (String?) -> Unit) {
        leftTree?.prefix(visitor)
        visitor(data)
        rightTree?.prefix(visitor)
    }

    /**
     * Visits the Tree items in post-order (first left subtree, then the right one and then the root).
     * @param visitor void callback to perform when visiting the node
     */
    fun postfix(visitor: (String?) -> Unit) {
        leftTree?.postfix(visitor)
        rightTree?.postfix(visitor)
        visitor(data)
    }

    /**
     * Given a Level (deep), visits all the Nodes in that level
     *
     * @param visitor void callback to perform when visiting the node
     */
    fun visitNodesAtLevel(visitor: (String?) -> Unit, targetLevel: Int, currentLevel: Int = 0) {
        if (currentLevel == targetLevel) {
            visitor(data)
        } else {
            leftTree?.visitNodesAtLevel(visitor, targetLevel, currentLevel + 1)
            rightTree?.visitNodesAtLevel(visitor, targetLevel, currentLevel + 1)
        }
    }

    /**
     * Returns the deep of the tree
     *
     * @return The deep of the tree
     */
    fun height(): Int {
        if (leftTree == null && rightTree == null) {
            return 0
        } else {
            val leftTreeHeight = leftTree?.height() ?: 0
            val rightTreeHeight = rightTree?.height() ?: 0
            return 1 + Math.max(leftTreeHeight, rightTreeHeight)
        }
    }

    /**
     * Visits the node of the trees in BFS (Breath, level by level)
     * @param visitor void callback to perform when visiting the node
     */
    fun bfs(visitor: (String?) -> Unit) {
        val queue = ArrayDeque<MerkleTree>()
        queue.addLast(this)

        while (!queue.isEmpty()) {
            val node = queue.pollFirst();
            visitor(node.data)

            val left = node.leftTree
            if (left != null) {
                queue.addLast(left)
            }

            val right = node.rightTree
            if (right != null) {
                queue.addLast(right)
            }
        }
    }

    /**
     * Add items to the tree following BFS algorithm (first free slot will be used)
     * @param item The Item to add into the Tree
     */
    fun addItemInBfs(item: String) {
        val queue = ArrayDeque<MerkleTree>()

        if (data == null) {
            data = item
            return
        }

        queue.addLast(this)
        var found = false
        while (!queue.isEmpty()) {
            val node = queue.pollFirst()

            if (!found) {
                if (node.leftTree == null) {
                    node.leftTree = MerkleTree(item)
                    node.data = node.leftTree!!.data!!.toString().sha512()
                    found = true
                    //return
                } else {
                    queue.addLast(node.leftTree)
                }

                if (node.rightTree == null) {
                    node.rightTree = MerkleTree(item)
                    node.data = node.leftTree!!.data!!.toString().sha512() + node.rightTree!!.data!!.toString().sha512()
                    found = true
                    //return
                } else {
                    queue.addLast(node.rightTree)
                }
            } else {
                node.data = node.leftTree!!.data!!.toString().sha512() + node.rightTree!!.data!!.toString().sha512()
            }
        }
    }


}