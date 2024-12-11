FXGL学习笔记

### 目录结构

- resource
  - assets
    - textures(存储图像)
    - sounds(短音频)
    - music(长音频)
    - text(文本格式内容)
    - json(有效的json数据)
    - tmx(tmx格式地图数据)
    - scripts(运行脚本)
    - properties (键值集合，例如系统配置对象 ".properties")
    - kv (类似properties，但文件解析为自定义数据结构 ".kv")
    - ai (gdxAi 行为树 ".tree")
    - data (用户自定义的任意资源 ".*")
    - ui/css (设置 UI 元素样式的 CSS 文件 ".css")
    - ui/fonts (UI 使用的字体 ".ttf", ".otf")
    - ui/icons (任务栏或窗口标题的图标图像 ".icon", ".jpg", ".png")
    - ui/cursors (光标图像，可用于替换默认鼠标光标 ".jpg", ".png")

### 流程分析

创建游戏的流程