package com.seokwon.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class DataSourceAuthDto implements Serializable {

    private String url;
    private String username;
    private String password;

}
