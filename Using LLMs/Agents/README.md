# LangChain AI Agent for Python Program Generation

## Project Overview

This project demonstrates the use of LangChain, LangGraph, and OpenAI's AzureChatOpenAI for creating an AI agent that generates and refines Python programs based on queries to CSV data. The agent follows a workflow managed by a state graph to initialize, generate, execute, handle errors, reflect on errors, and regenerate Python programs.

## Features

- **State Graph**: Utilizes LangGraph to manage the workflow states and transitions.
- **OpenAI Integration**: Uses AzureChatOpenAI to generate Python code based on natural language prompts and to reflect on errors.
- **Program Generation**: Automatically generates Python programs to query CSV files.
- **Error Handling**: Checks for errors in program outputs and suggests fixes through reflection.
- **Regeneration**: Revises Python programs based on reflections to improve functionality.

## Technologies Used

- LangChain Core
- LangChain OpenAI
- LangGraph
- Python
- OpenAI API (AzureChatOpenAI)
- Regular Expressions (re module)

## Installation

1. Install dependencies:
   ```bash
   pip install langchain_core langchain_openai langgraph
   ```

2. Clone or download the project files.

## Workflow

The workflow consists of several stages managed by LangGraph:

1. **Initialize**: Loads initial input data from files and sets up the agent state.
2. **Generate Query Program**: Creates a Python program based on the query to be executed on a CSV file.
3. **Execute Program**: Runs the generated Python program and captures its output.
4. **Check for Errors**: Validates the program's output and determines if errors occurred.
5. **Reflect on Errors**: Analyzes errors and suggests potential fixes through human-like reflection.
6. **Regenerate Program**: Updates the Python program based on the reflection for improved execution.

## Usage

To run the AI agent:

```bash
python langchain_agent.py
```

Ensure the `query_input.txt` and corresponding query files (`{query_name}_query.txt`) are correctly formatted and accessible in the project directory.

## Output

After execution, the agent generates several output files:

- `{query_name}.py`: Final Python program generated or revised based on reflections.
- `{query_name}.txt`: Output of the executed Python program.
- `{query_name}_errors.txt`: Detailed error messages, if any.
- `{query_name}_reflect.txt`: Reflections on errors and suggested fixes.

## Notes

- Customize the `AZURE_OPENAI_API_KEY`, `AZURE_OPENAI_ENDPOINT`, and other configurations in `langchain_agent.py` as per your Azure OpenAI setup.
- Adjust the workflow stages, prompts, and error handling mechanisms in `langchain_agent.py` for different use cases or datasets.