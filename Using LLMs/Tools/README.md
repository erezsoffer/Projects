# Azure OpenAI Agent for Data Extraction and Analysis

## Project Overview

This project showcases an Azure OpenAI agent capable of extracting entities from text files, performing internet searches, generating Python programs for data analysis, executing those programs, and writing content to files. The agent uses OpenAI's GPT-4 model for natural language processing and DuckDuckGo's search API for web queries.

## Features

- **Entity Extraction**: Extracts entities of a specific type from text files using Azure OpenAI's language model.
- **Internet Search**: Searches the web using DuckDuckGo's API and retrieves attributes of entities based on search results.
- **Program Generation**: Generates Python programs for analyzing CSV files, tailored to specific analysis requests.
- **Program Execution**: Executes generated Python programs and captures output files.
- **File Operations**: Writes content to specified files based on program outputs or user input.

## Technologies Used

- Azure OpenAI (via `openai` Python package)
- DuckDuckGo Search API (via `duckduckgo_search` Python package)
- Python 3
- Requests library
- Pandas library (for data handling)

## Installation

1. Install dependencies:
   ```bash
   pip install openai duckduckgo_search pandas
   ```

2. Clone or download the project files.

## Usage

To use the Azure OpenAI agent for data extraction and analysis:

1. Ensure you have set up Azure OpenAI credentials (`api_key` and `azure_endpoint`) in your environment.
2. Prepare an input JSON file (`input.json`) specifying the query and file resources for the agent.
3. Run the Python script (`agent.py`) with the input JSON file:
   ```bash
   python agent.py
   ```

## Workflow

The agent follows these main functions in its workflow:

- **Entity Extraction**: Reads a text file and extracts entities of a specified type using Azure OpenAI.
- **Internet Search**: Uses DuckDuckGo to search for an attribute of a given entity and extracts the answer using Azure OpenAI.
- **Program Generation**: Generates Python code for analyzing CSV files based on specific analysis requests using Azure OpenAI.
- **Program Execution**: Executes generated Python programs and captures output files.
- **File Operations**: Writes content to specified files based on program outputs or user input.

## Input JSON Structure

The input JSON file (`input.json`) should include:
- `query_name`: Name of the query or task.
- `file_resources`: List of resources (e.g., file names) required for the task.

Example `input.json`:
```json
{
"query_name": "query_example.txt",
"file_resources": [
 "data_file.csv",
 "input_file.txt"
]
}
```

## Output

The agent generates output based on the tasks specified in the input JSON file:
- Extracted entities or search results.
- Python programs for data analysis.
- Output files containing analysis results.

## Notes

- Customize the `api_key` and `azure_endpoint` in `agent.py` according to your Azure OpenAI configuration.
- Adjust the functions and prompts in `agent.py` for different use cases or datasets.