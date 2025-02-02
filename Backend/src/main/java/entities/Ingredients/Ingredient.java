package entities.Ingredients;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The unique identifier for the ingredient", example = "1")
    private int iid;

    @Schema(description = "The identifier of the ingredient from the supplier or external system", example = "A12345")
    private String offid;

    @Schema(description = "The name of the ingredient", example = "Tomato")
    private String iname;

    @Schema(description = "The energy content of the ingredient per 100 grams", example = "18.0")
    private Double energyPer100g;

    @Schema(description = "The fat content of the ingredient per 100 grams", example = "0.2")
    private Double fatPer100g;

    @Schema(description = "The saturated fat content of the ingredient per 100 grams", example = "0.1")
    private Double saturatedFatPer100g;

    @Schema(description = "The carbohydrates content of the ingredient per 100 grams", example = "3.9")
    private Double carbohydratesPer100g;

    @Schema(description = "The sugar content of the ingredient per 100 grams", example = "2.5")
    private Double sugarsPer100g;

    @Schema(description = "The protein content of the ingredient per 100 grams", example = "0.9")
    private Double proteinPer100g;

    @Schema(description = "The salt content of the ingredient per 100 grams", example = "0.02")
    private Double saltPer100g;

    @Schema(description = "The fiber content of the ingredient per 100 grams", example = "1.2")
    private Double fiberPer100g;

    public Ingredient(String iname, Double energyPer100g) {
        this.iname = iname;
        this.offid = "";
        this.energyPer100g = 0.0;
        this.fatPer100g = 0.0;
        this.saturatedFatPer100g = 0.0;
        this.carbohydratesPer100g = 0.0;
        this.sugarsPer100g = 0.0;
        this.proteinPer100g = 0.0;
        this.saltPer100g = 0.0;
        this.fiberPer100g = 0.0;
    }

    public Ingredient() {
    }

    @Schema(description = "Gets the ingredient id for the ingredient")
    public int getIid() {
        return iid;
    }

    @Schema(description = "Gets the Open Food Facts API id for the ingredient")
    public String getOffid() {
        return offid;
    }

    @Schema(description = "Sets the Open Food Facts API id for the ingredient")
    public void setOffid(String offid) {
        this.offid = offid;
    }

    @Schema(description = "Gets the name for the ingredient")
    public String getIname() {
        return iname;
    }

    @Schema(description = "Sets the name for the ingredient")
    public void setIname(String iname) {
        this.iname = iname;
    }

    @Schema(description = "Gets the energy per 100g for the ingredient")
    public Double getEnergyPer100g() {
        return energyPer100g;
    }

    @Schema(description = "Sets the energy per 100g for the ingredient")
    public void setEnergyPer100g(Double energyPer100g) {
        this.energyPer100g = energyPer100g;
    }

    @Schema(description = "Gets the Fat per 100g for the ingredient")
    public Double getFatPer100g() {
        return fatPer100g;
    }

    @Schema(description = "Sets the Fat per 100g for the ingredient")
    public void setFatPer100g(Double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    @Schema(description = "Gets the Saturated Fat per 100g for the ingredient")
    public Double getSaturatedFatPer100g() {
        return saturatedFatPer100g;
    }

    @Schema(description = "Sets the Saturated Fat per 100g for the ingredient")
    public void setSaturatedFatPer100g(Double saturatedFatPer100g) {
        this.saturatedFatPer100g = saturatedFatPer100g;
    }

    @Schema(description = "Gets the Carbohydrates per 100g for the ingredient")
    public Double getCarbohydratesPer100g() {
        return carbohydratesPer100g;
    }

    @Schema(description = "Sets the Carbohydrates per 100g for the ingredient")
    public void setCarbohydratesPer100g(Double carbohydratesPer100g) {
        this.carbohydratesPer100g = carbohydratesPer100g;
    }

    @Schema(description = "Gets the Sugar per 100g for the ingredient")
    public Double getSugarsPer100g() {
        return sugarsPer100g;
    }

    @Schema(description = "Sets the Sugar per 100g for the ingredient")
    public void setSugarsPer100g(Double sugarsPer100g) {
        this.sugarsPer100g = sugarsPer100g;
    }

    @Schema(description = "Gets the Protein per 100g for the ingredient")
    public Double getProteinPer100g() {
        return proteinPer100g;
    }

    @Schema(description = "Sets the Protein per 100g for the ingredient")
    public void setProteinPer100g(Double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    @Schema(description = "Gets the Salt per 100g for the ingredient")
    public Double getSaltPer100g() {
        return saltPer100g;
    }

    @Schema(description = "Sets the Salt per 100g for the ingredient")
    public void setSaltPer100g(Double saltPer100g) {
        this.saltPer100g = saltPer100g;
    }

    @Schema(description = "Gets the Fiber per 100g for the ingredient")
    public Double getFiberPer100g() {
        return fiberPer100g;
    }

    @Schema(description = "Sets the Fiber per 100g for the ingredient")
    public void setFiberPer100g(Double fiberPer100g) {
        this.fiberPer100g = fiberPer100g;
    }
}