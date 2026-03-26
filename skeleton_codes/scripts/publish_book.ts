const API_BASE_URL = 'http://localhost:8080/api/books';

export type BookCondition = 'NEW' | 'LIKE_NEW' | 'VERY_GOOD' | 'GOOD' | 'ACCEPTABLE';

export interface BookCreateRequest {
  ownerId: number;
  stock: number;
  price: number;
  title: string;
  author: string;
  genre: string;
  edition: string;
  condition: BookCondition;
  publishedYear: number;
  description: string;
  isbn: string;
  coverImageUrl: string;
}

export interface Book extends BookCreateRequest {
    bookId: number;
    reviewCount: number;
    totalScore: number;
    isActive: boolean;
    datePosted: string;
}

export async function submitBookListing(bookData: BookCreateRequest) {
  try {
    const response = await fetch('http://localhost:8080/api/books', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(bookData),
    });

    if (response.ok) {
      const createdBook = await response.json();
      console.log('Successfully listed book:', createdBook);
      return createdBook;
    } else {
      throw new Error(`Failed to create listing: ${response.statusText}`);
    }
  } catch (error) {
    console.error('Network error during book submission:', error);
    throw error;
  }
}

export async function getSellerListings(ownerId: number): Promise<Book[]> {
  const response = await fetch(`${API_BASE_URL}/owner/${ownerId}`);
  if (!response.ok) throw new Error('Failed to fetch seller listings');
  return response.json();
}

export async function getBookById(bookId: number): Promise<Book> {
  const response = await fetch(`${API_BASE_URL}/${bookId}`);
  if (!response.ok) throw new Error(`Failed to fetch book with ID ${bookId}`);
  return response.json();
}

export async function updateBookListing(bookId: number, updateData: BookCreateRequest): Promise<Book> {
  const response = await fetch(`${API_BASE_URL}/${bookId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(updateData),
  });
  if (!response.ok) throw new Error(`Failed to update book with ID ${bookId}`);
  return response.json();
}

export async function deleteBookListing(bookId: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/${bookId}`, {
    method: 'DELETE',
  });
  if (!response.ok) throw new Error(`Failed to delete book with ID ${bookId}`);
}