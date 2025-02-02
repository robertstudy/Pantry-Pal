package entities.Images;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import java.sql.Blob;

@Entity
@Schema(description = "Represents an image associated with either an ingredient or a recipe step in the system.")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "The unique identifier for the image", example = "1")
    private int id;

    @Lob  // specifies that the field should be stored as a large object (BLOB)
    @Column(nullable = false)
    @Schema(description = "The binary image data stored as a Blob", example = "image data in binary format")
    private Blob data;  // Store image data as a Blob

    @Column(nullable = true)
    @Schema(description = "The Ingredient ID associated with the image. This can be null if not related to an ingredient.", example = "123")
    private Integer iid;  // Ingredient ID

    @Column(nullable = true)
    @Schema(description = "The Recipe ID associated with the image. This can be null if not related to a recipe.", example = "456")
    private Integer rid;  // Recipe ID

    @Column(nullable = true)
    @Schema(description = "The step number in the recipe for the image. This can be null if not related to a specific recipe step.", example = "2")
    private Integer step;  // Recipe step number

    // Default constructor
    public Image() {}

    @Schema(description = "Gets the id of the image")
    public int getId() {
        return id;
    }

    @Schema(description = "Sets the id of the image")
    public void setId(int id) {
        this.id = id;
    }

    @Schema(description = "Gets the Blob (Binary Large Object) data of the image")
    public Blob getData() {
        return data;
    }

    @Schema(description = "Sets the Blob (Binary Large Object) data of the image")
    public void setData(Blob data) {
        this.data = data;
    }

    @Schema(description = "Gets the ingredient id of the image")
    public Integer getIid() {
        return iid;
    }

    @Schema(description = "Sets the ingredient id of the image")
    public void setIid(Integer iid) {
        this.iid = iid;
    }

    @Schema(description = "Gets the recipe id of the image")
    public Integer getRid() {
        return rid;
    }

    @Schema(description = "Sets the recipe id of the image")
    public void setRid(Integer rid) {
        this.rid = rid;
    }

    @Schema(description = "Gets the step number of the image for the recipe its linked to")
    public int getStep() {
        return step;
    }

    @Schema(description = "Sets the step number of the image for the recipe its linked to")
    public void setStep(int step) {
        this.step = step;
    }
}