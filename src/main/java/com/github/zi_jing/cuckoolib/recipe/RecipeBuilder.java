package com.github.zi_jing.cuckoolib.recipe;

import com.github.zi_jing.cuckoolib.CuckooLib;
import com.github.zi_jing.cuckoolib.material.SolidShape;
import com.github.zi_jing.cuckoolib.material.type.Material;
import com.github.zi_jing.cuckoolib.util.ValidateResult;
import com.github.zi_jing.cuckoolib.util.math.MathUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

public class RecipeBuilder {
    protected RecipeMap recipeMap;
    protected List<IngredientIndex> inputs;
    protected List<ChanceEntry> outputs;
    protected List<FluidStack> fluidInputs, fluidOutputs;
    protected Map<RecipeValueFormat, Object> values;
    protected ValidateResult result;

    public RecipeBuilder() {
        this.inputs = NonNullList.<IngredientIndex>create();
        this.outputs = NonNullList.<ChanceEntry>create();
        this.fluidInputs = new ArrayList<FluidStack>();
        this.fluidOutputs = new ArrayList<FluidStack>();
        this.values = new HashMap<RecipeValueFormat, Object>();
        this.result = ValidateResult.VALID;
    }

    protected RecipeBuilder(RecipeMap recipeMap) {
        this();
        this.recipeMap = recipeMap;
    }

    protected RecipeBuilder(Recipe recipe) {
        this(recipe.getRecipeMap());
        this.inputs.addAll(recipe.getInputs());
        this.outputs.addAll(recipe.getOutputs());
        this.fluidInputs.addAll(recipe.getFluidInputs());
        this.fluidOutputs.addAll(recipe.getFluidOutputs());
        this.values.putAll(recipe.getValues());
    }

    protected RecipeBuilder(RecipeBuilder builder) {
        this(builder.getRecipeMap());
        this.inputs.addAll(builder.getInputs());
        this.outputs.addAll(builder.getOutputs());
        this.fluidInputs.addAll(builder.getFluidInputs());
        this.fluidOutputs.addAll(builder.getFluidOutputs());
        this.values.putAll(builder.getValues());
        this.result = builder.result;
    }

    public RecipeBuilder copy() {
        return new RecipeBuilder(this);
    }

    public Recipe build(int id) {
        this.validate();
        if (this.result == ValidateResult.VALID) {
            Recipe recipe = new Recipe(this.recipeMap, this.inputs, this.outputs, this.fluidInputs, this.fluidOutputs);
            this.recipeMap.addRecipe(id, recipe);
            return recipe;
        }
        CuckooLib.getLogger().error("Failed to build the recipe with validate result [ " + this.result + " ]");
        return null;
    }

    protected void validate() {
        if (!MathUtil.between(this.inputs.size(), this.recipeMap.getMinInput(), this.recipeMap.getMaxInput())) {
            CuckooLib.getLogger().error("Input size [ " + this.inputs.size() + " ] isn't between [ "
                    + this.recipeMap.getMinInput() + " ] and [ " + this.recipeMap.getMaxInput() + " ]");
            this.result = ValidateResult.ERROR;
        }
        if (!MathUtil.between(this.outputs.size(), this.recipeMap.getMinOutput(), this.recipeMap.getMaxOutput())) {
            CuckooLib.getLogger().error("Output size [ " + this.outputs.size() + " ] isn't between [ "
                    + this.recipeMap.getMinOutput() + " ] and [ " + this.recipeMap.getMaxOutput() + " ]");
            this.result = ValidateResult.ERROR;
        }
        if (!MathUtil.between(this.fluidInputs.size(), this.recipeMap.getMinFluidInput(),
                this.recipeMap.getMaxFluidInput())) {
            CuckooLib.getLogger().error("Fluid input size [ " + this.fluidInputs.size() + " ] isn't between [ "
                    + this.recipeMap.getMinFluidInput() + " ] and [ " + this.recipeMap.getMaxFluidInput() + " ]");
            this.result = ValidateResult.ERROR;
        }
        if (!MathUtil.between(this.fluidOutputs.size(), this.recipeMap.getMinFluidOutput(),
                this.recipeMap.getMaxFluidOutput())) {
            CuckooLib.getLogger().error("Fluid output size [ " + this.fluidOutputs.size() + " ] isn't between [ "
                    + this.recipeMap.getMinFluidOutput() + " ] and [ " + this.recipeMap.getMaxFluidOutput() + " ]");
            this.result = ValidateResult.ERROR;
        }
    }

    public RecipeMap getRecipeMap() {
        return this.recipeMap;
    }

    public List<IngredientIndex> getInputs() {
        return this.inputs;
    }

    public List<ChanceEntry> getOutputs() {
        return this.outputs;
    }

    public List<FluidStack> getFluidInputs() {
        return this.fluidInputs;
    }

    public List<FluidStack> getFluidOutputs() {
        return this.fluidOutputs;
    }

    public Map<RecipeValueFormat, Object> getValues() {
        return this.values;
    }

    public RecipeBuilder inputItem(ItemStack... input) {
        for (int i = 0; i < input.length; i++) {
            if (input[i] == null || input[i].isEmpty()) {
                CuckooLib.getLogger().error("The recipe input ItemStack can't be empty");
                this.result = ValidateResult.ERROR;
            } else {
                this.inputs.add(IngredientIndex.from(input[i]));
            }
        }
        return this;
    }

    public RecipeBuilder inputItem(Collection<ItemStack> input) {
        return this.inputItem(input.toArray(new ItemStack[0]));
    }

    public RecipeBuilder inputNotConsumeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            CuckooLib.getLogger().error("The recipe input ItemStack can't be empty");
            this.result = ValidateResult.ERROR;
        } else {
            this.inputs.add(IngredientIndex.from(stack, 0));
        }
        return this;
    }

    public RecipeBuilder input(IngredientIndex... input) {
        return this.input(Arrays.asList(input));
    }

    public RecipeBuilder input(Collection<IngredientIndex> input) {
        this.inputs.addAll(input);
        return this;
    }

    public RecipeBuilder inputOre(String oreDict) {
        this.inputs.add(IngredientIndex.from(oreDict));
        return this;
    }

    public RecipeBuilder inputOre(String oreDict, int count) {
        this.inputs.add(IngredientIndex.from(oreDict, count));
        return this;
    }

    public RecipeBuilder inputOre(SolidShape shape, Material material) {
        this.inputs.add(IngredientIndex.from(shape, material));
        return this;
    }

    public RecipeBuilder inputOre(SolidShape shape, Material material, int count) {
        this.inputs.add(IngredientIndex.from(shape, material, count));
        return this;
    }

    public RecipeBuilder outputItem(ItemStack... output) {
        for (int i = 0; i < output.length; i++) {
            if (output[i] == null || output[i].isEmpty()) {
                CuckooLib.getLogger().error("The recipe output ItemStack can't be empty");
                this.result = ValidateResult.ERROR;
            } else {
                this.outputs.add(new ChanceEntry(output[i]));
            }
        }
        return this;
    }

    public RecipeBuilder outputItem(Collection<ItemStack> output) {
        return this.outputItem(output.toArray(new ItemStack[0]));
    }

    public RecipeBuilder outputChancedItem(ItemStack stack, double chance) {
        if (stack == null || stack.isEmpty()) {
            CuckooLib.getLogger().error("The recipe output ItemStack can't be empty");
            this.result = ValidateResult.ERROR;
        } else {
            this.outputs.add(new ChanceEntry(stack, chance));
        }
        return this;
    }

    public RecipeBuilder fluidInput(FluidStack... input) {
        for (int i = 0; i < input.length; i++) {
            if (input[i] == null) {
                CuckooLib.getLogger().error("The recipe fluid input FluidStack can't be null");
                this.result = ValidateResult.ERROR;
            } else {
                this.fluidInputs.add(input[i]);
            }
        }
        return this;
    }

    public RecipeBuilder fluidInputs(Collection<FluidStack> input) {
        return this.fluidInput(input.toArray(new FluidStack[0]));
    }

    public RecipeBuilder fluidOutputs(FluidStack... output) {
        for (int i = 0; i < output.length; i++) {
            if (output[i] == null) {
                CuckooLib.getLogger().error("The recipe fluid output FluidStack can't be null");
                this.result = ValidateResult.ERROR;
            } else {
                this.fluidOutputs.add(output[i]);
            }
        }
        return this;
    }

    public RecipeBuilder fluidOutputs(Collection<FluidStack> output) {
        return this.fluidOutputs(output.toArray(new FluidStack[0]));
    }

    public <T> RecipeBuilder addValue(RecipeValueFormat<T> format, T value) {
        if (format.validate(value)) {
            this.values.put(format, value);
        }
        return this;
    }
}