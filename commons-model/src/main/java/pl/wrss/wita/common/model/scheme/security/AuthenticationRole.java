package pl.wrss.wita.common.model.scheme.security;

public interface AuthenticationRole {

    String getName();
    void setName(String name);

    String getCode();
    void setCode(String code);

    String[] getPermissionGranters();
    void setPermissionGranters(String[] permissionGranters);
}
