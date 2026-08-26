# KineticDummy

[简体中文](#简体中文) | [English](#english)

## 简体中文

### 模组定位

**KineticDummy** 是 Kinetic 系列的战斗测试假人模块，用于测试武器、附魔、套装、属性、仆从和各种伤害来源，而不是只提供一个简单的静态靶子。

### 主要功能

- **准星召唤**：通过命令在玩家视线目标位置生成测试假人。
- **一键清理**：快速清除当前世界的测试假人。
- **实时伤害统计**：统计本次伤害、总伤害、瞬时 DPS、平均 DPS 和命中次数。
- **伤害来源识别**：显示伤害来源、伤害类型，并能处理仆从给主人显示伤害数据的场景。
- **范围广播**：可控制附近哪些玩家能看到假人的战斗数据。
- **智能休眠**：附近无人时进入低干扰休眠状态，降低无意义的实体交互与伤害处理。
- **掉血但不真正死亡**：可开启真实掉血测试，并在停止攻击一段时间后自动恢复。
- **无敌帧切换**：方便测试高频攻击、连击和持续伤害。
- **环境伤害切换**：可决定假人是否接受非玩家环境伤害。
- **属性编辑**：在 GUI 中直接调整假人属性数值，适合验证极端属性组合。
- **装备与 Curios 编辑**：可给假人快速设置护甲、手持物与 Curios 饰品。
- **装备黑名单**：服主可禁止某些装备被放入假人。
- **客户端 HUD**：支持浮动伤害、头顶 HUD、死亡/结算信息等显示设置。
- **Jade 兼容**：安装 Jade 后可显示额外测试假人信息。

### 常用命令

- `/kt dummy help`
- `/kt dummy spawn`
- `/kt dummy clear`

### 配置文件

```text
config/kineticcore/dummy_server.toml
config/kineticcore/dummy_client.toml
```

服务端配置负责休眠范围、广播范围和装备限制；客户端配置负责伤害数字、HUD、颜色和缩放等显示偏好。

### 运行环境

- Minecraft 1.20.1
- Minecraft Forge 47.x
- Java 17
- KineticCore：必须
- Curios：必须
- Jade：可选

## English

### Overview

**KineticDummy** is the combat testing module of the Kinetic family. It is designed for testing weapons, enchantments, armor sets, attributes, minions and different damage sources with live statistics.

### Key Features

- Crosshair-targeted dummy spawning and world cleanup commands.
- Total damage, hit damage, instant DPS, average DPS and hit-count tracking.
- Damage source/type identification and owner-aware minion reporting.
- Configurable broadcast range.
- Automatic standby behavior when no players are nearby.
- Optional health loss without permanent death.
- Toggleable invulnerability frames and environmental damage.
- Runtime attribute editing.
- Equipment and Curios editing with blacklist support.
- Client floating-damage, overhead HUD and summary display options.
- Optional Jade integration.

### Configuration

```text
config/kineticcore/dummy_server.toml
config/kineticcore/dummy_client.toml
```

### Requirements

- Minecraft 1.20.1
- Minecraft Forge 47.x
- Java 17
- KineticCore: required
- Curios: required
- Jade: optional


## 开源协议与版权 (License)

Copyright (C) 2024-2026 XYAT.

本项目基于 **GNU Lesser General Public License v3.0 (LGPLv3)** 协议开源。

This project is open-sourced under the **GNU Lesser General Public License v3.0 (LGPLv3)**.
