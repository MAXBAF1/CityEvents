package com.example.cityevents

import androidx.lifecycle.*
import com.example.cityevents.dataBase.CellEntity
import com.example.cityevents.dataBase.MainDb
import com.example.cityevents.mapbox.location.LocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class MainViewModel(db: MainDb) : ViewModel() {
    private val dao = db.getDao()
    val locUpdates = MutableLiveData<LocationModel>()
    val capturedCells = dao.getAllCells().asLiveData()

    class ViewModelFactory(private val db: MainDb) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(db) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun insertNewCellInDatabase(cell: CellEntity) {
        viewModelScope.launch(Dispatchers.Default) {
            dao.insertCell(cell)
        }
    }

    fun clearDatabase() {
        viewModelScope.launch(Dispatchers.Default) {
            dao.clear()
        }
    }

    fun getAllCellsFormDatabase(callback: (Flow<List<CellEntity>>) -> Unit) {
        viewModelScope.launch {
            callback(dao.getAllCells())
        }
    }
}