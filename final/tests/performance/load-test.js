import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '2m', target: 100 }, // Ramp-up to 100 users
    { duration: '5m', target: 100 }, // Stay at 100 users (Sustained)
    { duration: '1m', target: 0 },   // Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1500'], // Latency p95 < 500ms, p99 < 1.5s
    http_req_failed: ['rate<0.01'],               // Error rate < 1%
  },
};

const BASE_URL = 'http://localhost:8080/api'; // API Gateway

export default function () {
  // 1. Browse Catalog
  let catalogRes = http.get(`${BASE_URL}/catalog/products`);
  check(catalogRes, { 'catalog status is 200': (r) => r.status === 200 });

  sleep(1);

  // 2. Add to Cart
  let cartPayload = JSON.stringify({ productId: 1, quantity: 1 });
  let cartParams = { headers: { 'Content-Type': 'application/json' } };
  let cartRes = http.post(`${BASE_URL}/cart/items`, cartPayload, cartParams);
  check(cartRes, { 'cart status is 201': (r) => r.status === 201 });

  sleep(1);
}
