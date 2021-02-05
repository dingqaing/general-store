package com.me.generalstore.redis.dao;

import com.me.generalstore.redis.model.article.Article;

import java.util.List;

/**
 * Article DAO.
 *
 * @author dqq
 * @date 2021 /2/4
 */
public interface ArticleDao {

    /**
     * 发布文章.
     *
     * @param user  the user
     * @param title the title
     * @param link  the link
     * @return the string
     */
    String postArticle(String user, String title, String link);

    /**
     * 获取文章.
     *
     * @param page  the page
     * @param order the order
     * @return the article
     */
    List<Article> getArticle(int page, String order);

    /**
     * 文章投票.
     *
     * @param userId  the user id
     * @param article the article 例：article: 1234
     */
    void voteArticle(String userId, String article);
}
