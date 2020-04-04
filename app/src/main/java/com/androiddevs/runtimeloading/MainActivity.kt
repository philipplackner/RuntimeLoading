package com.androiddevs.runtimeloading

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val url = "https://firebasestorage.googleapis.com/v0/b/fir-tutorials-837f1.appspot.com/o/allosaurus.glb?alt=media&token=90aeb3e1-594e-4e06-9d04-630e03fe6796"
    private lateinit var arFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFragment = fragment as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            spawnObject(hitResult.createAnchor(), Uri.parse(url))
        }
    }

    private fun spawnObject(anchor: Anchor, modelUri: Uri) {
        val renderableSource = RenderableSource.builder()
            .setSource(this, modelUri, RenderableSource.SourceType.GLB)
            .setRecenterMode(RenderableSource.RecenterMode.ROOT)
            .build()
        ModelRenderable.builder()
            .setSource(this, renderableSource)
            .setRegistryId(modelUri)
            .build()
            .thenAccept {
                addNodeToScene(anchor, it)
            }.exceptionally {
                Log.e("MainActivity", "Error loading model", it)
                null
            }
    }

    private fun addNodeToScene(anchor: Anchor, modelRenderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        TransformableNode(arFragment.transformationSystem).apply {
            renderable = modelRenderable
            setParent(anchorNode)
        }
        arFragment.arSceneView.scene.addChild(anchorNode)
    }
}
