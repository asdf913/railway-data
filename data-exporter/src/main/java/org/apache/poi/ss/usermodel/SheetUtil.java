package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellRangeAddress;

public interface SheetUtil {

	static void setAutoFilter(final Sheet sheet) {
		//
		if (sheet != null) {
			//
			final int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
			//
			Row row = null;
			//
			int maxCellCount = 0;
			//
			for (int i = 0; i < physicalNumberOfRows; i++) {
				//
				if ((row = sheet.getRow(i)) == null) {
					continue;
				}
				maxCellCount = Integer.max(maxCellCount, row.getLastCellNum() - row.getFirstCellNum());
				//
			}
			//
			sheet.setAutoFilter(new CellRangeAddress(0, sheet.getLastRowNum(), 0, maxCellCount));
			//
		} // if
			//
	}

}