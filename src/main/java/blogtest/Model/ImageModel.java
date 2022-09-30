package blogtest.Model;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;

@Data
@Entity
public class ImageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fileName;
    @Lob
    @Column(columnDefinition = "bytea")
    private byte[] imageBytes;
    @JsonIgnore
    private Long userId;
    @JsonIgnore
    private Long postId;
}
