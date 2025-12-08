const API_BASE = process.env.NEXT_PUBLIC_API_BASE;

if (!API_BASE) {
  // Fail fast during dev if the base URL is missing
  throw new Error("Missing NEXT_PUBLIC_API_BASE in .env.local");
}

export type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

type FetchOptions = {
  method?: HttpMethod;
  headers?: Record<string, string>;
  body?: unknown;
  cache?: RequestCache;
};

/**
 * Small wrapper around fetch to centralize base URL and JSON handling.
 */
export async function apiFetch<TResponse = unknown>(
  path: string,
  { method = "GET", headers, body, cache = "no-store" }: FetchOptions = {}
): Promise<TResponse> {
  const url = `${API_BASE}${path.startsWith("/") ? path : `/${path}`}`;

  const response = await fetch(url, {
    method,
    headers: {
      "Content-Type": "application/json",
      ...headers,
    },
    body: body ? JSON.stringify(body) : undefined,
    cache,
  });

  if (!response.ok) {
    const detail = await safeJson(response);
    const message = detail?.message || response.statusText;
    throw new Error(`API ${response.status}: ${message}`);
  }

  return (await safeJson(response)) as TResponse;
}

async function safeJson(res: Response) {
  try {
    return await res.json();
  } catch {
    return undefined;
  }
}
