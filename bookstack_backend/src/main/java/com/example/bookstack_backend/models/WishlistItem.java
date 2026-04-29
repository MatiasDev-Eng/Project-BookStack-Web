package com.example.bookstack_backend.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wishlist_items")
@NoArgsConstructor
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    public WishlistItem(Wishlist wishlist, Book book) {
        this.wishlist = wishlist;
        this.book = book;
    }

    public Long getWishlistItemId() { return wishlistItemId; }
    public void setWishlistItemId(Long wishlistItemId) { this.wishlistItemId = wishlistItemId; }

    public Wishlist getWishlist() { return wishlist; }
    public void setWishlist(Wishlist wishlist) { this.wishlist = wishlist; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
}