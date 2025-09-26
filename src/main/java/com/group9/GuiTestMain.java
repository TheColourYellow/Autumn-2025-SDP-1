
package com.group9;

import com.group9.util.Database;
import com.group9.view.BookstoreView;

public class GuiTestMain {
    public static void main(String[] args) {
        Database.init();
        BookstoreView.launch(BookstoreView.class, args);
    }
}
