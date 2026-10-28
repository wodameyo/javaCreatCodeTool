import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

const apiBase = '${frontend.apiBasePath}'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      [apiBase]: {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => (path.startsWith(apiBase) ? path.slice(apiBase.length) : path)
      }
    }
  }
})
