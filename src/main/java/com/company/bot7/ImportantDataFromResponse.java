package com.company.bot7;

class ImportantDataFromResponse {
    private final boolean FAILURE;
    private final String RESPONSE;
    private final int STATUSCODE;

    public ImportantDataFromResponse(boolean FAILURE, String RESPONSE, int STATUSCODE){
        this.FAILURE = FAILURE;
        this.RESPONSE = RESPONSE;
        this.STATUSCODE = STATUSCODE;
    }

    public String getRESPONSE() {
        return RESPONSE;
    }

    public int getSTATUSCODE() {
        return STATUSCODE;
    }

    public boolean isFAILURE() {
        return FAILURE;
    }
}
