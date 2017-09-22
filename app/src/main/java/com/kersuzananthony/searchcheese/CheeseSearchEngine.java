package com.kersuzananthony.searchcheese;


import java.util.ArrayList;
import java.util.List;

class CheeseSearchEngine {

    private final List<String> mCheeses;

    CheeseSearchEngine(List<String> cheeses) {
        mCheeses = cheeses;
    }

    public List<String> search(final String query) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> result = new ArrayList<>();

        for (String item : mCheeses) {
            if (item.contains(query)) {
                result.add(item);
            }
        }

        return result;
    }

}
