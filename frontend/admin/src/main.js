import './assets/index.css'
import './assets/styles/variables.css'
import '@fortawesome/fontawesome-free/css/all.min.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// import './styles/index.scss'
// import './permission'

import App from './App.vue'
import router from './router'

// 导入自定义组件
import Components from './components'

// 导入权限指令
import permissionDirective from './directives/permission'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(ElementPlus, {
  locale: zhCn,
})
app.use(router)

// 注册自定义组件
app.use(Components)

// 注册权限指令
app.use(permissionDirective)

app.mount('#app')
