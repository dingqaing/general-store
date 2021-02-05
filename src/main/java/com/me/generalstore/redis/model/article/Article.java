package com.me.generalstore.redis.model.article;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 文章 实体类.
 *
 * @author dqq
 * @date 2021 /2/3
 */
@Setter
@Getter
@NoArgsConstructor
public class Article implements Serializable {

    private String title;

    private String link;

    private String poster;

    private String time;

    private String votes;


}
