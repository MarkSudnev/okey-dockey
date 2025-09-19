import { render, screen } from '@testing-library/react';
import App from './App';

test("renders search box", () => {
  render(<App />);
  const inputField = screen.getByPlaceholderText(/What is this document about/i);
  const searchButton = screen.getByText(/search/i, {selector: "button"});
  expect(inputField).toBeInTheDocument();
  expect(searchButton).toBeInTheDocument();
});
