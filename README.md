# Information Retrieval Project

Project Topic #2

Cookbook
----
1. Preprocess your document collection (e.g., 20 News Groups)
   - Stopword removal, lemmatization/stemming
2. Build the inverted index
   - For each term, store the list of documents in which it appears together with frequency
3. Given a query, fetch all documents that contain at least one query term
4. Implement classic probabilistic ranking functions and rank the documents with each
   - BIM, Two Poisson, BM11, BM25
5. Implement the computation of LM with smoothing
   - Compute local language models of all documents
   - Compute the global language model for the whole collection
   - Compute the ranking function by combining local and global probabilities (e.g., using Jelinek-Mercer smoothing scheme)
   - Rank the documents according to obtained conditional query probabilities
