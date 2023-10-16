package us.alphakilo;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static us.alphakilo.Constants.*;

@SpringBootApplication
@Slf4j
public class AKIECompanionApplication implements CommandLineRunner {

    @Value("${userProfile: none}")
    private String userProfile;

    private final Map<String, Item> items = new HashMap<>();

    public static void main(String[] args)  {
        SpringApplication.run(AKIECompanionApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        String esoSavedVariablesSubDir = "Documents\\Elder Scrolls Online\\live\\SavedVariables";

        String akTestLua = Files.readString(Paths.get(userProfile, esoSavedVariablesSubDir, "AKInventoryExporter.lua"));

    	Globals globals = JsePlatform.standardGlobals();
        globals.load(akTestLua).call();
        LuaValue akaTestData = globals.get("AKInventoryExporter_Data");

        parseIIDataLua(akaTestData);

        log.info("done");

        generateReport();
	}

    private void generateReport() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        CreationHelper creationHelper = workbook.getCreationHelper();

        Sheet itemsSheet = workbook.createSheet("Items");
        itemsSheet.setAutoFilter(new CellRangeAddress(0,0, 0, 11));
        itemsSheet.setColumnWidth(0, 12000);
        itemsSheet.setColumnWidth(1, 7000);
        itemsSheet.setColumnWidth(2, 7000);
        itemsSheet.setColumnWidth(3, 3000);
        itemsSheet.setColumnWidth(4, 6000);
        itemsSheet.setColumnWidth(5, 6000);
        itemsSheet.setColumnWidth(6, 6000);
        itemsSheet.setColumnWidth(7, 2000);
        itemsSheet.setColumnWidth(8, 6000);
        itemsSheet.setColumnWidth(9, 6000);
        itemsSheet.setColumnWidth(10, 2500);
        itemsSheet.setColumnWidth(11, 2500);

        itemsSheet.createFreezePane(0,1);

        int rowNumer = 0;
        Row header = itemsSheet.createRow(rowNumer);

        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(font);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setFontName("Arial");
        font2.setFontHeightInPoints((short) 10);
        font2.setBold(true);
        style.setFont(font2);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle style2 = workbook.createCellStyle();
        style2.cloneStyleFrom(style);
        style2.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        header.setRowStyle(headerStyle);
        addCell(header, 0, "Item Name", null);
        addCell(header, 1, "Type", null);
        addCell(header, 2, "Item Set", null);
        addCell(header, 3, "Quality", null);
        addCell(header, 4, "Trait", null);
        addCell(header, 5, "Location", null);
        addCell(header, 6, "Location Type", null);
        addCell(header, 7, "Account", null);
        addCell(header, 8, "Style", null);
        addCell(header, 9, "Outfit Style", null);
        addCell(header, 10, "Count", null);
        addCell(header, 11, "Price", null);

        for (var entry:items.entrySet()) {
            for (var loc : entry.getValue().getLocations()) {
                rowNumer++;
                Row itemRow = itemsSheet.createRow(rowNumer);
                itemRow.setRowStyle(getStyleForQuality(rowNumer % 2 == 0 ? style : style2, (entry.getValue().getQuality()), workbook));
                XSSFHyperlink link = (XSSFHyperlink) creationHelper.createHyperlink(HyperlinkType.URL);
                link.setAddress( "https://ts.uesp.net/esolog/itemLinkImage.php?itemid=" + entry.getValue().getId() );
                addCell(itemRow, 0, entry.getValue().getName(), link);
                addCell(itemRow, 1, StringUtils.isNotBlank(entry.getValue().getType()) ? entry.getValue().getType() : "",null);
                addCell(itemRow, 2, StringUtils.isNotBlank(entry.getValue().getSetName()) ? entry.getValue().getSetName() : "",null );
                addCell(itemRow, 3, ITEM_QUALITIES.getOrDefault(entry.getValue().getQuality(), ""), null);
                addCell(itemRow, 4, TRAITS.getOrDefault(entry.getValue().getTrait(), ""), null);
                addCell(itemRow, 5, loc.getName(), null);
                addCell(itemRow, 6, loc.getType(), null);
                addCell(itemRow, 7, entry.getValue().getAccount(), null);
                addCell(itemRow, 8, entry.getValue().getStyle(), null);
                addCell(itemRow, 9, entry.getValue().getOutfitStyle(), null);
                addCell(itemRow, 10, String.valueOf(loc.getQuantity()), null);
                if(entry.getValue().getPrice() > 0 ) {
                    addCell(itemRow, 11, entry.getValue().getPrice(), true);
                } else {
                    addCell(itemRow, 11, "", null);
                }
            }
        }

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "ESO Inventory Report.xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
    }

    private CellStyle getStyleForQuality(CellStyle baseStyle, int qualityId, XSSFWorkbook workbook) {
        CellStyle newStyle = workbook.createCellStyle();
        newStyle.cloneStyleFrom(baseStyle);
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        switch(qualityId) {
            case 0:
                font.setColor(HSSFColor.HSSFColorPredefined.GREY_50_PERCENT.getIndex());
                break;
            case 1:
                font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
                break;
            case 2:
                font.setColor(HSSFColor.HSSFColorPredefined.GREEN.getIndex());
                break;
            case 3:
                font.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
                break;
            case 4:
                font.setColor(HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
                break;
            case 5:
                font.setColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
                break;
            case 6:
                font.setColor(HSSFColor.HSSFColorPredefined.ORANGE.getIndex());
                break;
        }
        newStyle.setFont(font);
        return newStyle;
    }

    private void addCell(Row row, int column, String value, XSSFHyperlink link) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(row.getRowStyle());
        if(link != null) {
            cell.setHyperlink(link);
            cell.setCellValue("[" + value + "]");
        } else {
            cell.setCellValue( value );
        }
    }

    private void addCell(Row row, int column, long value, boolean currency) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        if(currency) {
            CellStyle cellStyle = ((XSSFCellStyle) row.getRowStyle()).copy();
            cell.setCellStyle(cellStyle);
            cellStyle.setDataFormat((short) 5 );
        } else {
            cell.setCellStyle(row.getRowStyle());
        }
    }

    private void parseLocations(LuaValue luaTable, Item item) {
        LuaValue key = LuaValue.NIL;
        while(true) {
            Varargs n = luaTable.next(key);
            key = n.arg1();
            if (key.isnil()) {
                break;
            }
            LuaValue key2 = LuaValue.NIL;
            var location = new Location();
            while(true) {
                Varargs n2 = n.arg(2).next(key2);
                key2 = n2.arg1();
                if (key2.isnil()) {
                    break;
                }
                switch (key2.tojstring()) {
                    case "bagId":
                        location.setBagId(n2.arg(2).toint());
                        break;
                    case "id":
                        location.setId(n2.arg(2).tojstring());
                        break;
                    case "locationType":
                        location.setType(n2.arg(2).tojstring());
                        break;
                    case "locationName":
                        location.setName(n2.arg(2).tojstring());
                        break;
                    case "slotId":
                        location.setSlotId(n2.arg(2).toint());
                        break;
                    case "locationQuantity":
                        location.setQuantity(n2.arg(2).toint());
                        break;
                }
            }
            item.getLocations().add(location);
        }
    }

    private void parseItemData(LuaValue luaTable, String accountId) {
        LuaValue key = LuaValue.NIL;
        while(true) {
            Item item = new Item();
            Varargs n = luaTable.next(key);
            key = n.arg1();
            if (key.isnil()) {
                break;
            }
            item.setId(key.tojstring());
            item.setAccount(accountId);
            LuaValue value = n.arg(2);
            LuaValue key2 = LuaValue.NIL;
            while (true) {
                Varargs n2 = value.next(key2);
                key2 = n2.arg1();
                if (key2.isnil()) {
                    break;
                }
                LuaValue value2 = n2.arg(2);
                switch(key2.tojstring()) {
                    case "itemQuality":
                        item.setQuality(value2.toint());
                        break;
                    case "itemName":
                        item.setName(value2.tojstring());
                        break;
                    case "setName":
                        item.setSetName(value2.tojstring());
                        break;
                    case "trait":
                        item.setTrait(value2.toint());
                        break;
                    case "outfitStyleId":
                        item.setOutfitStyle(value2.tojstring());
                        break;
                    case "style":
                        item.setStyle(value2.tojstring());
                        break;
                    case "itemType":
                        item.setType(value2.tojstring());
                        break;
                    case "price":
                        item.setPrice(value2.tolong());
                        break;
                    case "locations":
                        parseLocations(value2, item);
                        break;
                }
            }
            items.put(item.getId(), item);
        }
    }
    private void parseAccountWide(LuaValue luaTable, String accountId) {
        LuaValue key = LuaValue.NIL;
        while(true) {
            Varargs n = luaTable.next(key);
            key = n.arg1();
            if (key.isnil()) {
                break;
            } else if(StringUtils.equals(key.tojstring(), "data")) {
                //Item Data
                parseItemData(n.arg(2), accountId);
            }
        }
    }
    private void parseAccount(LuaValue luaTable, String accountId) {
        LuaValue key = LuaValue.NIL;
        while(true) {
            Varargs n = luaTable.next(key);
            key = n.arg1();
            if (key.isnil()) {
                break;
            }
            key = n.arg1();
            LuaValue value = n.arg(2);

            if(StringUtils.equals(key.tojstring(), "$AccountWide")) {
                parseAccountWide(value, accountId);
            } else {
                //TODO: Toon
            }

        }
    }
    private void parseIIDataLua(LuaValue luaTable) {
        if (!luaTable.istable()) {
            throw new RuntimeException("Not a LuaTable");
        }

        Varargs n = luaTable.next(LuaValue.NIL);
        LuaValue key = n.arg1();
        LuaValue value = n.arg(2);
        n = value.next(LuaValue.NIL);
        key = n.arg1();
        value = n.arg(2);

        parseAccount(value, key.tojstring());
    }
}
