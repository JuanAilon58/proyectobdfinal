import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '10s', target: 100 },  // Normal load
    { duration: '30s', target: 2000 }, // SPIKE to 2000 concurrent users
    { duration: '1m', target: 2000 },  // Sustained spike
    { duration: '10s', target: 100 },  // Scale down
    { duration: '10s', target: 0 },
  ],
};

const BASE_URL = 'http://localhost:8080/api';

export default function () {
  http.get(`${BASE_URL}/catalog/products`);
  sleep(0.1);
}
