package com.example.project3.Store

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.R

class StoreActivity : AppCompatActivity() {

    private lateinit var storeRecyclerView: RecyclerView
    private lateinit var inventoryRecyclerView: RecyclerView
    private lateinit var storeAdapter: StoreAdapter
    private lateinit var invenAdapter: InvenAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        storeRecyclerView = findViewById(R.id.store_recyclerview)
        inventoryRecyclerView = findViewById(R.id.inven_recyclerview)

        storeAdapter = StoreAdapter(getStoreItems())
        invenAdapter = InvenAdapter(getInventoryItems())

        storeRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        storeRecyclerView.adapter = storeAdapter

        inventoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        inventoryRecyclerView.adapter = invenAdapter
    }

    private fun getStoreItems(): List<String> {
        // 예시 데이터, 실제 데이터로 대체
        return listOf("Item 1", "Item 2", "Item 3")
    }

    private fun getInventoryItems(): List<String> {
        // 예시 데이터, 실제 데이터로 대체
        return listOf("Item A", "Item B", "Item C")
    }
}
