# ethereum-wallet-tracker

Java-based backend service that monitors Ethereum wallet activity via blockchain explorer APIs
and sends real-time transaction notifications to a Telegram bot.

## Features
- Monitors Ethereum wallet transactions
- Detects incoming and outgoing ETH transfers
- Validates transaction execution status (success / error)
- Stores processed transactions to avoid duplicates
- Sends formatted notifications via Telegram bot

## Tech Stack
- Java
- Java HTTP Client
- Jackson (JSON parsing)
- Ethereum blockchain explorer API
- Telegram Bot API
- SQLite (local transaction storage)

## Architecture Overview
- Periodically fetches recent transactions from blockchain explorer API
- Parses JSON responses into domain objects
- Compares incoming transactions with stored records
- Sends notifications only for newly detected events

## Configuration
This project requires external API keys and credentials.

Set the following environment variables before running:
- `ETHERSCAN_API_KEY`
- `TELEGRAM_BOT_TOKEN`
- `TELEGRAM_CHAT_ID`
- `ETHEREUM_WALLET_ADDRESS`

No sensitive data is included in this repository.

## Project Status
This project is an early-stage prototype created primarily for learning and experimentation.
Active development on the Ethereum version is currently paused.

A future version targeting the Solana blockchain is planned.

## License
This project is released under a custom license.
See the `LICENSE` file for details.

## Disclaimer
This software is provided as-is and should not be used as financial or investment advice.
