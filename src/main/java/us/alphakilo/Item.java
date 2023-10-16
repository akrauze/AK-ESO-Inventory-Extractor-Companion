package us.alphakilo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Item {

    private String id;
    private String name;
    private int quality;
    private String account;
    private int trait;
    private String outfitStyle;
    private String style;
    private String setName;
    private String type;
    private long price;
    private List<Location> locations = new ArrayList<>();

}
