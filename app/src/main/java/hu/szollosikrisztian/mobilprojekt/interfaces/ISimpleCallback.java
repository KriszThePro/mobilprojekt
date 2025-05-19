package hu.szollosikrisztian.mobilprojekt.interfaces;

public interface ISimpleCallback {

    void onSuccess(Object data);

    void onFailure(Exception e);
}
