package com.HoangDucTa.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info( title = "Social Network API"),
        tags = {
                @Tag(name = "1. login"),
                @Tag(name = "2.	Quản lý thông tin cá nhân"),
                @Tag(name = "3.	đăng bài"),
                @Tag(name = "4. Bình luận"),
                @Tag(name = "5.	Like bài post"),
                @Tag(name = "6. Bạn bè"),
                @Tag(name = "7. Báo cáo")
        }
)
public class OpenAPIConfig {

}
