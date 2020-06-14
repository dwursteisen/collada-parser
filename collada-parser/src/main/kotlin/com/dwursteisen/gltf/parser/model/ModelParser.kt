package com.dwursteisen.gltf.parser.model

import collada.Transformation
import com.adrienben.tools.gltf.models.GltfAsset
import com.adrienben.tools.gltf.models.GltfMesh
import com.adrienben.tools.gltf.models.GltfNode
import com.dwursteisen.gltf.parser.support.isEmissiveTexture
import com.dwursteisen.gltf.parser.support.toFloatArray
import com.dwursteisen.gltf.parser.support.toIntArray
import com.dwursteisen.gltf.parser.support.transformation
import com.dwursteisen.minigdx.scene.api.model.*

class ModelParser(private val gltfAsset: GltfAsset) {

    fun objects(): Map<String, Model> {
        val nodes = gltfAsset.nodes.filter { it.mesh != null }
        return nodes.map { it.toObject() }
            .map { it.name to it }
            .toMap()
    }

    private fun GltfNode.toObject(): Model {
        return Model(
            name = name!!,
            transformation = Transformation(transformation.asGLArray().toFloatArray()),
            mesh = this.mesh!!.toMesh()
        )
    }

    private fun GltfMesh.toMesh(): Mesh {
        val primitives = primitives.map { primitive ->
            val positions = primitive.attributes["POSITION"].toFloatArray()
                .toList()
                .chunked(3)
                .map { Position(it[0], it[1], it[2]) }

            val normals = primitive.attributes["NORMAL"].toFloatArray()
                .toList()
                .chunked(3)
                .map { Normal(it[0], it[1], it[2]) }

            val colors = primitive.attributes["COLOR_0"].toFloatArray()
                .toList()
                .chunked(4)
                .map { Color(it[0], it[1], it[2], it[3]) }

            val material = gltfAsset.materials.getOrNull(primitive.material.index)
            val uvs = if (!material.isEmissiveTexture()) {
                emptyList()
            } else {
                primitive.attributes["TEXCOORD_0"].toFloatArray()
                    .toList()
                    .chunked(2)
                    .map { UV(it[0], it[1]) }
            }
            val vertices = positions.mapIndexed { index, p ->
                val n = normals[index]
                val c = if (colors.isNotEmpty()) {
                    colors[index]
                } else {
                    Color.INVALID
                }

                val uv = if (uvs.isNotEmpty()) {
                    uvs[index]
                } else {
                    UV.INVALID
                }
                Vertex(
                    position = p,
                    normal = n,
                    color = c,
                    uv = uv
                )
            }

            val materialId = if (primitive.material.isEmissiveTexture()) {
                primitive.material.index
            } else {
                -1
            }
            Primitive(
                vertices = vertices,
                verticesOrder = primitive.indices.toIntArray(),
                materialId = materialId
            )
        }
        return Mesh(primitives)
    }
}
