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

// Function triggered when the user submits the "Add Book" form
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

export async function uploadImage(file: File): Promise<string> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch('http://localhost:8080/api/images/upload', {
        method: 'POST',
        body: formData,
    });

    if (!response.ok) throw new Error('Image upload failed');
    
    return response.text(); 
}

export async function handleFullBookSubmission(bookData: BookCreateRequest, imageFile: File | null) {
    let imageUrl = "";

    if (imageFile) {
        imageUrl = await uploadImage(imageFile);
    }

    const finalBookData = {
        ...bookData,
        coverImageUrl: imageUrl || bookData.coverImageUrl // Use new URL or fallback to default
    };

    return await submitBookListing(finalBookData);
}