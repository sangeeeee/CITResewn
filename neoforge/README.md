# CIT Resewn NeoForge 1.21.1

本目录是 `CIT Resewn` 的独立 NeoForge 构建入口，目标是 `Minecraft 1.21.1` 且不依赖 Fabric API 运行。

### 1) 构建

- 使用根目录工作区，执行：
  - `.\gradlew.bat -p neoforge clean build`
- 产物：
  - `neoforge\build\libs\citresewn-neoforge-<版本>.jar`

### 2) 本地运行（开发）

- `.\gradlew.bat -p neoforge runClient`

### 3) 说明

- 运行时元数据使用 `neoforge.mods.toml`，不是 `fabric.mod.json`。
- 入口与可扩展点使用 NeoForge API + ServiceLoader 重写，不走 Fabric Loader/Fabric API。
- 仅保留与项目构建/映射相关的 `net.fabricmc` 工件（如 Yarn 映射、Mixin 运行时/转换链路），
  并未引入 Fabric API 作为运行依赖。
