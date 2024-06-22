import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  preview: {
    port: 81,
    strictPort: true,
    host:'0.0.0.0',
    cors: true,
    proxy: {
      '/ws': {
        target: 'ws://app:8080',
        // target: 'ws://localhost:8080',
        ws: true,
        changeOrigin: true,
        secure: false
      }
    }
  }
})