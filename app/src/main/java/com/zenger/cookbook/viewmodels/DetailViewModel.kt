package com.zenger.cookbook.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenger.cookbook.api.models.RecipeInstruction
import com.zenger.cookbook.api.state.Result
import com.zenger.cookbook.repository.DataRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class DetailViewModel(application: Application) : ViewModel() {

    private val repository by lazy { DataRepository(application) }
    lateinit var outcome: LiveData<Result<List<RecipeInstruction>>>

    private val _ingredients by lazy { MutableLiveData<String>() }
    val ingredients: LiveData<String> = _ingredients

    private val _steps by lazy { MutableLiveData<String>() }
    val steps: LiveData<String> = _steps

    private var step: String = ""
    private var ingredient: String = ""

    fun getRecipeInstructions(id: Int) =
            viewModelScope.launch(IO) {
                outcome = repository.getRecipeInstructions(id)
            }

    fun parseInstructions(instructions: List<RecipeInstruction>) {
        instructions.map { instruction ->
            instruction.steps.map { recipeStep ->

                if (recipeStep.ingredients?.isNotEmpty() == true) {
                    ingredient = parseIngredients(recipeStep.ingredients)
                }

                step += "${recipeStep.number}: ${recipeStep.step} \n\n"
            }
        }
        _steps.value = step
        _ingredients.value = ingredient
    }

    private fun parseIngredients(
            ingredients: List<RecipeInstruction.RecipeStep.RecipeIngredients>): String {

        var index = 0
        var value = ""
        ingredients.map { item ->
            value += "${++index}: ${item.name} \n\n"
        }
        return value
    }


}