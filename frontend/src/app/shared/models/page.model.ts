/**
 * Estrutura padrão de resposta paginada.
 * Compatível com Spring Data / Pageable.
 *
 * @typeParam T Tipo do item contido na página
 */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // página atual (0-based)
  first: boolean;
  last: boolean;
}