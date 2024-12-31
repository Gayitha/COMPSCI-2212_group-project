package utils;

public class ErrorInfo {
    private int startIndex;
    private int endIndex;
    private String errorText;
    private String errorType;

    public ErrorInfo(int startIndex, int endIndex, String errorText, String errorType) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.errorText = errorText;
        this.errorType = errorType;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                ", errorText='" + errorText + '\'' +
                ", errorType='" + errorType + '\'' +
                '}';
    }
}
